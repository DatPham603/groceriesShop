package com.project.shopapp.Filter;

import com.project.shopapp.Components.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    //@Value của lombok dùng để tạo các trường bất biến (immutable fields) trong một lớp,Tạo một constructor cho các trường
    @Value("${api.prefix}")// org.springframework.beans.factory.annotation.Value;
    private String apiPrefix;
    private final UserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;
    // lấy ra token đã được login(đã xác thực), tạo ra dối tượng userService từ token đó
    // tạo tiếp UsernamePasswordAuthenticationToken để lưu vào SecurityContextHolder
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        // filterChain.doFilter(request,response); cho đi qua hết
        //Arrays.asList chuyển đổi một mảng thành một danh sách (List)+tạo danh sách từ các phần từ,là danh sách bất biến, 0 thẻ xóa hay thêm phần từ
        try {
            if (isPassByToken(request)) {
                filterChain.doFilter(request, response);
                return;
            }
            final String authHeader = request.getHeader("Authorization");//lấy ra gias trị từ  header cụ thể từ yêu cầu HTTP, ở đây là Authorization
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }
            final String token = authHeader.substring(7);
            final String phoneNumber = jwtUtils.extractClaims(token, Claims::getSubject);// lấy ra phonunber trong jwt bằng cách tham chiếu hàm truyền vào extractClaims
            if (phoneNumber != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(phoneNumber); // lấy ra userDetails từ phone trong token
                if (jwtUtils.validation(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    //setDetails():thiết lập chi tiết bổ sung cho Authentication (chẳng hạn như thông tin về yêu cầu HTTP hiện tại)
                    // .hữu ích khi muốn lưu thêm thông tin về phiên làm việc của người dùng.
                    // tóm lại thiết lập chi tiết bổ sung (ví dụ: địa chỉ IP, session ID) vào trong UsernamePasswordAuthenticationToken dựa trên thông tin yêu cầu HTTP.
                    //- Lưu đối tượng Authentication vào SecurityContextHolder:
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    // nếu xác thực thành công, đối tượng Authentication sẽ được lưu trữ trong SecurityContextHolder.
                    //- SecurityContextHolder không thực hiện xác thực mà chỉ lưu giữ ngữ cảnh bảo mật đã được xác thực,
                    //bao gồm thông tin về người dùng hiện tại và các quyền của họ.
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Unauthorized");
        }
    }

    private boolean isPassByToken(@NonNull HttpServletRequest request) {
        final List<Pair<String, String>> byPassTokens = Arrays.asList(
                Pair.of(String.format("%s/products", apiPrefix), "GET"),
                Pair.of(String.format("%s/categories", apiPrefix), "GET"),
                Pair.of(String.format("%s/users/login", apiPrefix), "POST"),
                Pair.of(String.format("%s/users/register", apiPrefix), "POST")
        );
        for (Pair<String, String> byPassToken : byPassTokens) {
            if (request.getServletPath().contains(byPassToken.getFirst()) &&
                    request.getMethod().equals(byPassToken.getSecond())) {
                return true;
            }
        }
        return false;
    }
}
