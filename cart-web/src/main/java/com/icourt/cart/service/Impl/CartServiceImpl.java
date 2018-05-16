package com.icourt.cart.service.Impl;

import com.icourt.cart.Enum.BussinessCartType;
import com.icourt.cart.config.CartConstant;
import com.icourt.cart.dao.CartDao;
import com.icourt.cart.dto.CartBussinessDTO;
import com.icourt.cart.dto.JudgementInfoDTO;
import com.icourt.cart.exception.CartServiceException;
import com.icourt.cart.service.CartService;
import com.icourt.cart.vo.CartBussinessVO;
import com.icourt.user.core.TokenUser;
import com.icourt.user.core.UserContext;
import com.icourt.user.service.ITokenProviderService;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Caomr on 2018/5/4.
 */
@Service
@Transactional
@Slf4j
public class CartServiceImpl implements CartService {


    @Resource
    ITokenProviderService tokenProviderService;

    @Autowired
    private CartDao cartDao;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public Pair getUserCartNumber() {
        TokenUser tokenUser = UserContext.getUser();
        String userId = tokenUser.getUserId();
        String officeId = tokenUser.getOfficeId();
        int userCartNumber = cartDao.getUserCartNumber(userId,officeId,"0");
        Pair pair = new Pair("userCartNumber",userCartNumber);
        return pair;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBussinessCart(List<CartBussinessVO> cartBussinessVOs) {
        TokenUser tokenUser = UserContext.getUser();

        // 测试
        tokenUser = new TokenUser();
        tokenUser.setUserId("test");
        tokenUser.setOfficeId("test");

        // 用户校验
        checkUser(tokenUser);
        // 数据校验
        checkCartBussinessVOs(cartBussinessVOs);
        String userId = tokenUser.getUserId();
        String officeId = tokenUser.getOfficeId();

        List<String> judgementList = new ArrayList<>();
        List<String> lawreguList = new ArrayList<>();
        List<String> judicialList = new ArrayList<>();
        for (CartBussinessVO CartBussinessVO:cartBussinessVOs) {
            if(BussinessCartType.JUDGEMENT.name().equals(CartBussinessVO.getBussinessType())){
                judgementList.add(CartBussinessVO.getBussinessId());
            } else if(BussinessCartType.LAWREGU.name().equals(CartBussinessVO.getBussinessType())){
                lawreguList.add(CartBussinessVO.getBussinessId());
            } else if(BussinessCartType.JUDICIAL.name().equals(CartBussinessVO.getBussinessType())){
                judicialList.add(CartBussinessVO.getBussinessId());
            }
        }
        if(judgementList.size() > 0){
            searchJudgement(judgementList, userId, officeId);
        }
        if(lawreguList.size() > 0){
            searchLawregu(lawreguList, userId, officeId);
        }
        if(judicialList.size() > 0){
            searchJudicial(judicialList, userId, officeId);
        }

    }

    private void checkCartBussinessVOs(List<CartBussinessVO> cartBussinessVOs) {
        for (CartBussinessVO cartBussinessVO:cartBussinessVOs) {
            Assert.notNull(cartBussinessVO,"存储信息含有空值");
            if(!BussinessCartType.JUDGEMENT.name().equals(cartBussinessVO.getBussinessType()) &&
                    !BussinessCartType.LAWREGU.name().equals(cartBussinessVO.getBussinessType()) &&
                    !BussinessCartType.JUDICIAL.name().equals(cartBussinessVO.getBussinessType())){
                throw new CartServiceException("数据类型不符合");
            }
        }
        if(cartBussinessVOs.size() > CartConstant.CART_MAX_SAVE){
            throw new CartServiceException("数据超量");
        }
    }

    private void checkUser(TokenUser tokenUser) {
        Assert.notNull(tokenUser,"用户数据不存在");
        Assert.notNull(tokenUser.getUserId(),"用户数据有误");
        Assert.notNull(tokenUser.getOfficeId(),"律所数据有误");
    }

    private void searchJudgement(List<String> judgementList, String userId, String officeId) {
        QueryBuilder query = QueryBuilders.termsQuery("jid", judgementList);
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder().withQuery(query)
                .withIndices(CartConstant.JUDGEMENT_INDICES).withTypes(CartConstant.JUDGEMENT_TYPE)
                .withPageable(new PageRequest(0,judgementList.size()));
        SearchQuery searchQuery = nativeSearchQueryBuilder.build();
        Page judgementPage = elasticsearchTemplate.queryForPage(searchQuery, Map.class);
        List<Map> content = judgementPage.getContent();
        for (Map data:content) {
            if(data == null){
                log.error("包含错误数据");
                throw new CartServiceException("包含错误数据");
            }
            CartBussinessDTO cartBussinessDTO = new CartBussinessDTO();
            JudgementInfoDTO judgementInfoDTO = new JudgementInfoDTO();
            cartBussinessDTO.setUserId(userId);
            cartBussinessDTO.setOfficeId(officeId);
            cartBussinessDTO.setBussinessId((String) data.get(CartConstant.JUDGEMENT_JID));
            cartBussinessDTO.setBussinessType(BussinessCartType.JUDGEMENT.name());
            cartBussinessDTO.setBussinessStatus("0");
            cartBussinessDTO.setBussinessTitle((String)data.get(CartConstant.JUDGEMENT_CASENAME));
            judgementInfoDTO.setJid((String) data.get(CartConstant.JUDGEMENT_JID));
            judgementInfoDTO.setTitle((String) data.get(CartConstant.JUDGEMENT_CASENAME));
            judgementInfoDTO.setCaseType((String) data.get(CartConstant.JUDGEMENT_CASENAME));
            judgementInfoDTO.setTitle((String) data.get(CartConstant.JUDGEMENT_CASENAME));
            judgementInfoDTO.setTitle((String) data.get(CartConstant.JUDGEMENT_CASENAME));
            judgementInfoDTO.setTitle((String) data.get(CartConstant.JUDGEMENT_CASENAME));

            Object paragraphs = data.get(CartConstant.JUDGEMENT_PARAGRAPHS);
            if(paragraphs != null && paragraphs instanceof List && ((List) paragraphs).size() > 0){
                Object text1 = ((List) paragraphs).get(0);
                if(text1 instanceof Map && ((Map) text1).get("text") != null){
                    cartBussinessDTO.setBussinessContent((String) ((Map) text1).get("text"));
                }
            }
            System.out.println(cartBussinessDTO.getUserId());
            System.out.println(cartBussinessDTO.getBussinessId());
            System.out.println(cartBussinessDTO.getBussinessType());
            System.out.println(cartBussinessDTO.getBussinessContent());

        }


//        Page judgmentPage = elasticsearchTemplate.queryForPage(searchQuery, JudgementInfoDTO.class, new SearchResultMapper() {
//            @Override
//            public <T> Page<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
//                List<JudgementInfoDTO> judgementDTOs = new ArrayList<>();
//                for(SearchHit searchHit:response.getHits().getHits()){
//                    JudgementInfoDTO judgementDTO = new JudgementInfoDTO();
//                    Map<String, Object> source = searchHit.getSource();
//                    judgementDTO.setJid(searchHit.getId());
//                    judgementDTO.setTitle((String) source.get(CartConstant.JUDGEMENT_CASENAME));
//                    judgementDTO.setCaseType((String) source.get(CartConstant.JUDGEMENT_LEVEL_CASE));
//                    judgementDTO.setJudgementType((Integer) source.get(CartConstant.JUDGEMENT_CASE_TYPE) == 1 ? "裁决" : "判定");
//                    judgementDTO.setCourtName((String) source.get(CartConstant.JUDGEMENT_COURT_NAME));
//                    judgementDTO.setCaseNumber((String) source.get(CartConstant.JUDGEMENT_CASENUMBER));
//                    judgementDTO.setJudgementDate((String) source.get(CartConstant.JUDGEMENT_JUDGEMENTINFO_DATE));
//                    Integer publishType = (Integer) source.get(CartConstant.JUDGEMENT_PUBLISH_TYPE);
//                    switch (publishType){
//                        case 0:
//                            judgementDTO.setPublicType("普通案例");
//                            break;
//                        case 1:case 5:case 6:
//                            judgementDTO.setPublicType("公报案例");
//                            break;
//                        case 2:case 3:case 4:
//                            judgementDTO.setPublicType("指导性案例");
//                            break;
//                        default:
//                            break;
//                    }
//                    Integer leveloftria = (Integer) source.get(CartConstant.JUDGEMENT_LEVELOFTRIA);
//                    switch (publishType){
//                        case 1:
//                            judgementDTO.setLeveloftria("一审");
//                            break;
//                        case 2:
//                            judgementDTO.setLeveloftria("二审");
//                            break;
//                        case 3:
//                            judgementDTO.setLeveloftria("再审");
//                            break;
//                        case 4:
//                            judgementDTO.setLeveloftria("执行");
//                            break;
//                        case -1:
//                            judgementDTO.setLeveloftria("其他");
//                            break;
//                        default:
//                            break;
//                    }
//                    judgementDTOs.add(judgementDTO);
//                }
//                if(judgementDTOs.size() > 0){
//                    return new PageImpl<T>((List<T>) judgementDTOs, null, judgementDTOs.size());
//                }
//                return null;
//            }
//        });
//        if(judgmentPage == null){
//            throw new CartServiceException();
//        }
//
//        List<JudgementInfoDTO> judgements = (List<JudgementInfoDTO>) judgmentPage.getContent();
//        System.out.println(judgements);
//        System.out.println(judgements.get(0).getJid());
    }

    private void searchLawregu(List<String> lawreguList, String userId, String officeId) {
        QueryBuilder query = QueryBuilders.termsQuery("jid", lawreguList);
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder().withQuery(query)
                .withIndices(CartConstant.JUDGEMENT_INDICES).withTypes(CartConstant.JUDGEMENT_TYPE)
                .withPageable(new PageRequest(0,lawreguList.size()));
    }

    private void searchJudicial(List<String> judicialList, String userId, String officeId) {
    }
}
