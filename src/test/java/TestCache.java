import com.google.common.cache.CacheBuilder;

/**
 * Created by zhangteng on 2018/8/27.
 */
public class TestCache {

    public static void main(String[] args){
        CacheBuilder.newBuilder()
                .maximumSize(2)
                .build();
    }
}
