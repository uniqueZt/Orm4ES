package com.framework.db.core.sql;

import com.framework.db.core.exception.ExecuteException;
import com.framework.db.core.model.cache.KeyValuePair;
import com.framework.db.core.model.mapper.Mapper;
import com.framework.db.core.parse.annotation.parameter.Key;
import com.framework.db.core.sql.aggregation.AggregationTree;
import com.framework.db.core.sql.aggregation.CommonAggregation;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.elasticsearch.search.aggregations.metrics.min.Min;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount;
import org.nlpcn.es4sql.domain.Field;
import org.nlpcn.es4sql.domain.KVValue;
import org.nlpcn.es4sql.domain.MethodField;
import org.nlpcn.es4sql.domain.Select;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangteng on 2018/8/21.
 */
public class AggregationSqlQueryParser extends AbstractSqlQueryParser {

    private Select select;

    private AggregationBuilder aggregationBuilder;

    private AggregationTree aggregationTree = new AggregationTree();

    public AggregationSqlQueryParser(Select select) {
        this.select = select;
    }

    @Override
    public SearchRequest buildSearchRequest() {
        SearchRequest searchRequest = initSearchRequest();
        try {
            setFieldMappingAlias(select);
            if(select.getWhere() != null){
               setWhere(searchRequest, select.getWhere());
            }
            //setLimit(searchRequest,select.getOffset(),select.getRowCount());
            setAggregation(searchRequest, select);
            setIndicesAndTypes(searchRequest, select);
        } catch (Exception e) {
            throw new ExecuteException("sql parse error", e);
        }
        return searchRequest;
    }

    private void setAggregation(SearchRequest searchRequest, Select select) {
        List<List<Field>> groupBys = select.getGroupBys();
        if (null != groupBys && groupBys.size() > 0) {
            AggregationTree currentAggregationTree = this.aggregationTree;
            AggregationBuilder currentAggregationBuilder = null;
            AggregationTree preAggregationTree = this.aggregationTree;
            AggregationBuilder preAggregationBuilder = null;
            boolean isFirst = true;
            for (Field field : groupBys.get(0)) {
                if (currentAggregationTree == null) {
                    currentAggregationTree = new AggregationTree();
                }
                if (currentAggregationBuilder == null) {
                    currentAggregationBuilder = AggregationBuilders.terms(field.getName() + "_groupBy").field(field.getName()).size(Integer.MAX_VALUE);
                }
                currentAggregationTree.setField(field.getName());
                if (isFirst) {
                    isFirst = false;
                    this.aggregationBuilder = currentAggregationBuilder;
                    preAggregationBuilder = currentAggregationBuilder;
                    currentAggregationTree = null;
                    currentAggregationBuilder = null;
                } else {
                    preAggregationTree.setSubAggregations(currentAggregationTree);
                    preAggregationTree = currentAggregationTree;
                    currentAggregationTree = null;
                    preAggregationBuilder.subAggregation(currentAggregationBuilder);
                    preAggregationBuilder = currentAggregationBuilder;
                    currentAggregationBuilder = null;
                }
            }
            List<Field> fields = select.getFields();
            List<CommonAggregation> commonAggreagtions = new LinkedList<>();
            for (Field field : fields) {
                if (field instanceof MethodField) {
                    MethodField methodField = (MethodField) field;
                    String type = methodField.getName();
                    if(!type.equals("script")){
                        List<KVValue> kvValues = methodField.getParams();
                        String column = kvValues.get(0).value.toString();
                        String key = type + "("+column+")";
                        CommonAggregation commonAggregation = new CommonAggregation();
                        commonAggregation.setAggsType(type);
                        commonAggregation.setAlias(key);
                        commonAggreagtions.add(commonAggregation);
                        AggregationBuilder aggregationBuilder = getAggregationBuilder(type, key,column);
                        preAggregationBuilder.subAggregation(aggregationBuilder);
                    }

                }
            }
            preAggregationTree.setCommonAggregations(commonAggreagtions);
            searchRequest.source().aggregation(aggregationBuilder);
            searchRequest.source().size(0);
        }
    }

    private AggregationBuilder getAggregationBuilder(String type, String key,String column) {
        AggregationBuilder aggregationBuilder = null;
        if (type.equals(CommonAggregation.SUM)) {
            aggregationBuilder = AggregationBuilders.sum(key).field(column);
        } else if (type.equals(CommonAggregation.MAX)) {
            aggregationBuilder = AggregationBuilders.max(key).field(column);
        } else if (type.equals(CommonAggregation.MIN)) {
            aggregationBuilder = AggregationBuilders.min(key).field(column);
        } else if (type.equals(CommonAggregation.COUNT)) {
            aggregationBuilder = AggregationBuilders.count(key).field(column);
        } else if (type.equals(CommonAggregation.AVG)) {
            aggregationBuilder = AggregationBuilders.avg(key).field(column);
        } else {
            throw new ExecuteException("目前不支持聚合 " + type);
        }
        return aggregationBuilder;
    }

    @Override
    public List getSqlResult(SearchResponse searchResponse, Mapper resultMapper) {
        List result = new LinkedList<>();
        Aggregations currentAggregations = searchResponse.getAggregations();
        AggregationTree currentAggregationTree = this.aggregationTree;
        //List<KeyValuePair> tempList = new LinkedList<>();
        parseSqlResult(currentAggregationTree,currentAggregations,null,result,resultMapper);
        return result;
    }



    private void parseSqlResult(AggregationTree currentAggregationTree, Aggregations currentAggregations, List<KeyValuePair> tempList, List result, Mapper resultMapper) {
        if(currentAggregationTree != null){
            if(currentAggregationTree.getSubAggregations() != null) {
                Terms terms = currentAggregations.get(currentAggregationTree.getField() + "_groupBy");
                List<? extends Terms.Bucket> buckets = terms.getBuckets();
                for (Terms.Bucket bucket : buckets) {
                     KeyValuePair keyValuePair = new KeyValuePair(currentAggregationTree.getField(),bucket.getKey());
                    parseSqlResult(currentAggregationTree.getSubAggregations(),bucket.getAggregations(),combineMidResult(tempList,keyValuePair),result,resultMapper);
                }
            }else{
                Terms terms = currentAggregations.get(currentAggregationTree.getField()+"_groupBy");
                List<? extends Terms.Bucket> buckets = terms.getBuckets();
                for(Terms.Bucket bucket:buckets){
                    KeyValuePair keyValuePair = new KeyValuePair(currentAggregationTree.getField(),bucket.getKey());
                    List<KeyValuePair> newMidResult = combineMidResult(tempList,keyValuePair);
                    Map<String,Object> temp = new HashMap<>();
                    List<CommonAggregation> commonAggregations = currentAggregationTree.getCommonAggregations();
                    for(CommonAggregation commonAggregation:commonAggregations){
                        if(commonAggregation.getAggsType().equals(CommonAggregation.SUM)){
                            Sum sum = bucket.getAggregations().get(commonAggregation.getAlias());
                            temp.put(commonAggregation.getAlias(),sum.getValue());
                        }
                        if(commonAggregation.getAggsType().equals(CommonAggregation.MAX)){
                            Max max = bucket.getAggregations().get(commonAggregation.getAlias());
                            temp.put(commonAggregation.getAlias(),max.getValue());
                        }
                        if(commonAggregation.getAggsType().equals(CommonAggregation.MIN)){
                            Min min = bucket.getAggregations().get(commonAggregation.getAlias());
                            temp.put(commonAggregation.getAlias(),min.getValue());
                        }
                        if(commonAggregation.getAggsType().equals(CommonAggregation.COUNT)){
                            ValueCount valueCount = bucket.getAggregations().get(commonAggregation.getAlias());
                            temp.put(commonAggregation.getAlias(),valueCount.getValue());
                        }
                        if(commonAggregation.getAggsType().equals(CommonAggregation.AVG)){
                            Avg avg = bucket.getAggregations().get(commonAggregation.getAlias());
                            temp.put(commonAggregation.getAlias(),avg.getValue());
                        }
                        if(null != newMidResult && newMidResult.size() > 0){
                            for(KeyValuePair tempKeyValuePair:newMidResult){
                                temp.put(tempKeyValuePair.getKey(),tempKeyValuePair.getValue());
                            }
                        }
                        try{
                            result.add(parseSingleResult(resultMapper,temp));
                        }catch (Exception e){
                            throw new ExecuteException("聚合结果解析失败",e);
                        }
                    }
                }
            }
        }
    }

    private List<KeyValuePair> combineMidResult(List<KeyValuePair> tempPropertyList,KeyValuePair keyValuePair){
        List<KeyValuePair> result = new LinkedList<>();
        result.add(keyValuePair);
        if(tempPropertyList != null){
            result.addAll(tempPropertyList);
        }
        return result;
    }

}


