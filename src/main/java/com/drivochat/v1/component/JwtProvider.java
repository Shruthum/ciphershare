package com.drivochat.v1.component;

import java.util.Date;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;


@Component
public class JwtProvider {

    private static final SecretKey secret = Jwts.SIG.HS256.key().build();

    public String generateToken(String username){

        return Jwts.builder()
                    .subject(username)
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + 120 * 60 * 1000))
                    .signWith(secret)
                    .compact();
    }

    public String extractUsername(String token){
        return extractClaim(token,Claims::getSubject);
    }

    public <T> T extractClaim(String token,Function<Claims,T> resolver){
         final CharSequence cstoken = new StringBuilder(token);
         final Claims claims = (Claims) Jwts.parser().verifyWith(secret).build().parse(cstoken).getPayload();
        return resolver.apply(claims);
    }

    public boolean validateToken(String username,String token){
        return username.equals(extractUsername(token)) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token){
        return extractClaim(token,Claims::getExpiration).before(new Date());
    }

}
