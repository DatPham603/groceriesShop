package com.project.shopapp.Components;

import com.project.shopapp.Configuration.MapperConfiguration;
import com.project.shopapp.Models.Token;
import com.project.shopapp.Models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import jakarta.websocket.Decoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.InvalidParameterException;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtUtils {
    @Value("${jwt.expiration}")
    private int expiration;
    @Value("${jwt.secretKey}")
    private String secretKey;
    public String generateToken(User user) throws Exception{
        Map<String , Object> claims = new HashMap<>();
        //this.generateSecretKey();
        claims.put("phoneNumber", user.getPhoneNumber());
        claims.put("userId",user.getId());
        try{
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(user.getUsername())
                    .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000l))
                    .signWith(getSignKey(), SignatureAlgorithm.HS256)
                    .compact();
            return token;
        }catch (Exception e){
            throw new InvalidParameterException("cant not create jwt : " + e.getMessage());
        }
    }
    private Key getSignKey(){
        byte[] bytes = Decoders.BASE64.decode(secretKey);//Decoders.BASE64.decode("wa57Phai0B0YuwWiW6xKIrxKozvquBm4PFgh6oLhvao=");
        return Keys.hmacShaKeyFor(bytes);//Keys.hmacShaKeyFor("wa57Phai0B0YuwWiW6xKIrxKozvquBm4PFgh6oLhvao=");
    }
//    private String generateSecretKey(){
//        SecureRandom secureRandom = new SecureRandom();
//        byte[] bytes = new byte[32];
//        secureRandom.nextBytes(bytes);
//        String secretKey = Encoders.BASE64.encode(bytes);
//        return secretKey;
//    }
    private Claims extractAllClaims(String token ){
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())// 0 cần truyền thuật toán kí vào vì nó tự biết trong header: "HS256"
                .build()
                .parseClaimsJws(token)// phân tích cú pháp 1 jwt đã được kí còn nếu dùng parseClaimsJws là chưa được kí
                .getBody();
    }
    public <T> T extractClaims(String token, Function<Claims, T> claimsResolve){
        final Claims claims = this.extractAllClaims(token);// lấy ra mọi claim
        return claimsResolve.apply(claims); // áp dụng hàm tham chiếu tới tập hợp claim này
    }
    public boolean tokenExpired(String token){
        Date expirationDate = this.extractClaims(token, Claims::getExpiration); // hàm tham chiếu tới hàm extractClaims để apply
        return expirationDate.before(new Date());
    }
    public boolean validation(String token, UserDetails userDetails){
        String phoneNumber = extractClaims(token,Claims::getSubject);
        return (phoneNumber.equals(userDetails.getUsername())) && !tokenExpired(token);
        //kiêm tra phonumber trong token có giống trong phonenunber của userdetail không
    }
}
