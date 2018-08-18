import org.elasticsearch.index.query.QueryBuilder;

import java.util.List;
import java.util.Map;

/**
 * Created by zhangteng on 2018/8/17.
 */
public interface DemoMapper {

    void insertTest(DemoBean demoBean);

    void udateTest(DemoBean demoBean);

    void deleteTest(String key);

    void deleteQueryTest(QueryBuilder queryBuilder);

    List<DemoBean> selectTest1(QueryBuilder queryBuilder);

    List<Map<String,Object>> selectTest2(QueryBuilder queryBuilder);

    List<DemoBean> sqlSelectTest();

}
