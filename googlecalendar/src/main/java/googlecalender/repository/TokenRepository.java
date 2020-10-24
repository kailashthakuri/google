package googlecalender.repository;

import googlecalender.entity.TokenInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<TokenInfo, Integer> {
    public TokenInfo findByUserId(String userId);
}
