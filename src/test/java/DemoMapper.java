import com.framework.db.core.parse.annotation.parameter.Key;
import com.framework.db.core.parse.annotation.parameter.Parameter;
import com.framework.db.core.parse.annotation.parameter.Query;
import org.elasticsearch.index.query.QueryBuilder;

import java.util.List;
import java.util.Map;

/**
 * Created by zhangteng on 2018/8/17.
 */
public interface DemoMapper {

    void insertTest(@Key String key, @Parameter DemoBean demoBean);

    void updateTest(@Key String key, @Parameter DemoBean demoBean);

    void deleteTest(@Key String key);

    void deleteQueryTest(@Query QueryBuilder queryBuilder);

    List<DemoBean> selectTest1(@Query QueryBuilder queryBuilder);

    List<Map<String,Object>> selectTest2(@Query QueryBuilder queryBuilder);

    List<DemoBean> sqlSelectTest(@Parameter(value = "actionName") String testName);

    List<Map<String,Object>> sqlSelectTest2();

}
