package com.icourt.cart.service.Impl;

import com.icourt.cart.Enum.BussinessCartType;
import com.icourt.cart.config.CartConstant;
import com.icourt.cart.dao.CartDao;
import com.icourt.cart.dto.CartBussinessDTO;
import com.icourt.cart.dto.JudgementDTO;
import com.icourt.cart.exception.CartServiceException;
import com.icourt.cart.service.CartService;
import com.icourt.cart.vo.CartBussinessVO;
import com.icourt.user.core.TokenUser;
import com.icourt.user.core.UserContext;
import javafx.util.Pair;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Caomr on 2018/5/4.
 */
@Service
@Transactional
public class CartServiceImpl implements CartService {

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
        List<String> judgementList = new ArrayList<>();
        List<String> lawreguList = new ArrayList<>();
        List<String> judicialList = new ArrayList<>();
        for (CartBussinessVO CartBussinessVO:cartBussinessVOs) {
            if(BussinessCartType.JUDGEMENT.name().equals(CartBussinessVO.getBussinessType())){
                judgementList.add(CartBussinessVO.getBussinessType());
            } else if(BussinessCartType.LAWREGU.name().equals(CartBussinessVO.getBussinessType())){
                lawreguList.add(CartBussinessVO.getBussinessType());
            } else if(BussinessCartType.JUDICIAL.name().equals(CartBussinessVO.getBussinessType())){
                judicialList.add(CartBussinessVO.getBussinessType());
            }
        }
        if(judgementList.size() > 0){
            searchJudgement(judgementList);
        }
        if(lawreguList.size() > 0){
            searchLawregu(lawreguList);
        }
        if(judicialList.size() > 0){
            searchJudicial(judicialList);
        }

    }

    private void searchJudgement(List<String> judgementList) {
        QueryBuilder query = QueryBuilders.termsQuery("jid", judgementList);
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder().withQuery(query)
                .withIndices(CartConstant.JUDGEMENT_INDICES).withTypes(CartConstant.JUDGEMENT_TYPE)
                .withPageable(new PageRequest(0,judgementList.size()));
        SearchQuery searchQuery = nativeSearchQueryBuilder.build();
        Page judgmentPage = elasticsearchTemplate.queryForPage(searchQuery, JudgementDTO.class, new SearchResultMapper() {
            @Override
            public <T> Page<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                List<JudgementDTO> judgementDTOs = new ArrayList<>();
                for(SearchHit searchHit:response.getHits().getHits()){
                    JudgementDTO judgementDTO = new JudgementDTO();
                    Map<String, Object> source = searchHit.getSource();
                    judgementDTO.setJid(searchHit.getId());
                    judgementDTO.setTitle((String) source.get(CartConstant.JUDGEMENT_CASENAME));
                    judgementDTO.setCaseType((String) source.get(CartConstant.JUDGEMENT_LEVEL_CASE));
                    judgementDTO.setJudgementType((Integer) source.get(CartConstant.JUDGEMENT_CASE_TYPE) == 1 ? "裁决" : "判定");
                    judgementDTO.setCourtName((String) source.get(CartConstant.JUDGEMENT_COURT_NAME));
                    judgementDTO.setCaseNumber((String) source.get(CartConstant.JUDGEMENT_CASENUMBER));
                    judgementDTO.setJudgementDate((String) source.get(CartConstant.JUDGEMENT_JUDGEMENTINFO_DATE));
                    Integer publishType = (Integer) source.get(CartConstant.JUDGEMENT_PUBLISH_TYPE);
                    switch (publishType){
                        case 0:
                            judgementDTO.setPublicType("普通案例");
                            break;
                        case 1:case 5:case 6:
                            judgementDTO.setPublicType("公报案例");
                            break;
                        case 2:case 3:case 4:
                            judgementDTO.setPublicType("指导性案例");
                            break;
                        default:
                            break;
                    }
                    Integer leveloftria = (Integer) source.get(CartConstant.JUDGEMENT_LEVELOFTRIA);
                    switch (publishType){
                        case 1:
                            judgementDTO.setLeveloftria("一审");
                            break;
                        case 2:
                            judgementDTO.setLeveloftria("二审");
                            break;
                        case 3:
                            judgementDTO.setLeveloftria("再审");
                            break;
                        case 4:
                            judgementDTO.setLeveloftria("执行");
                            break;
                        case -1:
                            judgementDTO.setLeveloftria("其他");
                            break;
                        default:
                            break;
                    }
                    judgementDTOs.add(judgementDTO);
                }
                if(judgementDTOs.size() > 0){
                    return new PageImpl<T>((List<T>) judgementDTOs, null, judgementDTOs.size());
                }
                return null;
            }
        });
        if(judgmentPage == null){
            throw new CartServiceException();
        }

        List<JudgementDTO> judgements = (List<JudgementDTO>) judgmentPage.getContent();
        System.out.println(judgements);
        System.out.println(judgements.get(0).getJid());
    }

    private void searchLawregu(List<String> lawreguList) {
        QueryBuilder query = QueryBuilders.termsQuery("jid", lawreguList);
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder().withQuery(query)
                .withIndices(CartConstant.JUDGEMENT_INDICES).withTypes(CartConstant.JUDGEMENT_TYPE)
                .withPageable(new PageRequest(0,lawreguList.size()));
    }

    private void searchJudicial(List<String> judicialList) {
    }
}
