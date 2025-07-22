package courseProject.fullSV.service.redis;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface BaseRedisService {
    void set(String key, Object value); // luu 1 gia tri kieu key-value vao redis
    void  setTimeToLive(String key, Duration time); // dat thoi gian song, neu qua thi redis se tu xoa
    void setValueTTL(String key, Object value, Duration time);
    void hashSet(String key, String field, Object value); // luu 1 field vao redis hash va luu gia tri cua field do
    boolean hasExisted(String key, String field);// kiem tra field hay column co ton tai hay khong
    Object get(String key);// lay gia tri value cua key kieu string
    Map<String, Object> getField(String key); //lay re field va value trong redis hash
    Object hashGet(String key, String field);// lay gia tri cua 1 field cu the trong redis hash
    List<Object> getHashByFieldPrefix(String key, String fieldPrefix); //lay tat ca field trong hash co ten bat dau bang prefix
    Set<String> getFieldPrefixes(String key);//trich xuat cac danh sach cac prefix co trong field cua hash
    void delete(String key); // xoa toan bo key
    void delete(String key, String field); //// xoa 1 field trong redis hash
    void delete(String key, List<String> field); // xoa nhieu field trong redis hash
}
