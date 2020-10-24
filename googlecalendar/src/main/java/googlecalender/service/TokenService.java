package googlecalender.service;


import googlecalender.dao.TokenDAO;
import googlecalender.dto.GoogleToken;
import googlecalender.entity.TokenInfo;
import googlecalender.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TokenService {


    @Autowired
    private TokenDAO tokenDAO;

    @Autowired
    private TokenRepository tokenRepository;

    public TokenInfo getTokenInfo(String userId) {
        return tokenRepository.findByUserId(userId);
    }

    public TokenInfo updateTokenInfo(TokenInfo tokenInfo) {
        return tokenRepository.save(tokenInfo);
    }

    public GoogleToken getToken(String userId) {
        Map<String, Object> token = this.tokenDAO.getToken(userId);
        GoogleToken googleToken = new GoogleToken();
        if (token.isEmpty()) {
            return null;
        }
        googleToken.setUserId(userId);
        googleToken.setRefreshToken(token.get("refresh_token") == null ? null : String.valueOf(token.get("refresh_token")));
        googleToken.setToken(token.get("token") == null ? null : String.valueOf(token.get("token")));
        return googleToken;
    }

    public int updateToken(GoogleToken googleToken) {
        return this.tokenDAO.insertToken(googleToken.getToken(), googleToken.getRefreshToken(), googleToken.getUserId());
    }

}
