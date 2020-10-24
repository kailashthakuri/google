package googlecalender.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class TokenDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int insertToken(String token, String refreshToken, String userId) {
        String sql = "update token set token=?, refresh_token=? where user_id=?";
//        final Map<String, Object> params = new HashMap<>();
//        params.put("token", token);
//        params.put("refreshToken", refreshToken);
//        params.put("userId", userId);
        return this.jdbcTemplate.update(sql, token, refreshToken, userId);
    }


    public Map<String, Object> getToken(String userId) {
        String sql = "SELECT token , refresh_token from  token where user_id=?";
        final Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        Map<String, Object> map = this.jdbcTemplate.queryForMap(sql, userId);
        return map;
    }
}
