package com.framework.db.core.filter;

import com.framework.db.core.exception.ExecuteException;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhangteng on 2018/9/24.
 */
public class FilterPipeLine {

    private List<Filter> filters = new LinkedList<>();

    public void addFilter(Filter filter){
        filters.add(filter);
    }

    public PipeLineResult doFilter(OperateEntity operateEntity){
        PipeLineResult pipeLineResult = new PipeLineResult();
        if(filters.size() == 0){
            pipeLineResult.setPassed(true);
            pipeLineResult.setFilterResult(null);
            pipeLineResult.setCurrentInvokeIndex(-1);
            return pipeLineResult;
        }
        int currentIndex = -1;
        FilterResult filterResult = null;
        for(int i=0;i<filters.size();i++){
            Filter currentFilter = filters.get(i);
            filterResult = currentFilter.beforeExecute(operateEntity);
            if(filterResult == null){
                throw new ExecuteException("filter返回不能为空");
            }
            currentIndex ++;
            if(!filterResult.isPassed()){
                break;
            }
        }
        pipeLineResult.setPassed(filterResult.isPassed());
        pipeLineResult.setFilterResult(filterResult.getResult());
        pipeLineResult.setCurrentInvokeIndex(currentIndex);
        return pipeLineResult;
    }

    public void afterFilter(OperateEntity operateEntiry,PipeLineResult pipelineResult){
        if(pipelineResult.getCurrentInvokeIndex() == -1){
            return;
        }
        int startIndex = pipelineResult.getCurrentInvokeIndex();
        if(!pipelineResult.isPassed()){
            startIndex = startIndex -1;
        }
        for(int i= startIndex;i>=0;i--){
            Filter currentFilter = filters.get(i);
            currentFilter.afterExecute(operateEntiry,pipelineResult.getFilterResult());
        }
    }

}
