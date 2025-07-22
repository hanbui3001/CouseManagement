package courseProject.fullSV.service.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class BaseRedisServiceImpl implements BaseRedisService{
    private RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, Object> hashOperations;
    @Autowired
    public BaseRedisServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
       this.hashOperations = this.redisTemplate.opsForHash();
    }

    @Override
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void setTimeToLive(String key, Duration time) {
        redisTemplate.expire(key,time);
    }

    @Override
    public void setValueTTL(String key, Object value, Duration time) {
        redisTemplate.opsForValue().set(key, value, time);
    }

    @Override
    public void hashSet(String key, String field, Object value) {
        hashOperations.put(key, field, value);
    }

    @Override
    public boolean hasExisted(String key, String field) {
        return hashOperations.hasKey(key, field);
    }

    @Override
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public Map<String, Object> getField(String key) {
       return hashOperations.entries(key);
    }

    @Override
    public Object hashGet(String key, String field) {
        return hashOperations.get(key, field);
    }

    @Override
    public List<Object> getHashByFieldPrefix(String key, String fieldPrefix) {
        List<Object> objects = new ArrayList<>();
        Map<String, Object> hashEntries = hashOperations.entries(key);
        for(Map.Entry<String, Object> entry : hashEntries.entrySet()){
            if(entry.getKey().startsWith(fieldPrefix)){
                objects.add(entry.getValue());
            }
        }
        return objects;
    }

    @Override
    public Set<String> getFieldPrefixes(String key) {
        return hashOperations.entries(key).keySet();
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public void delete(String key, String field) {
        hashOperations.delete(key, field);
    }

    @Override
    public void delete(String key, List<String> field) {
        for(String fields : field){
            hashOperations.delete(key, fields);
        }
    }
}
