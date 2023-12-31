package com.ss.util;

import java.security.Key;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ss.message.Constant;
import com.ss.user.web.MenuBean;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

public class JwtUtil {

	private static final String SECRET_KEY = "daf66e01593f61a15b857cf433aae03a005812b31234e149036bcc8dee755dbb";

	private static final long EXPIRATION_TIME = (5 * 60 * 60 * 1000); // 5 hours
	private static final Logger logger = LogManager.getLogger(JwtUtil.class);
	public static String generateToken(String subject,Long roleId) {
		// Create a map to store subject and roleId
		logger.info("In generateToken");
		logger.debug("generateToken " +subject +" "+roleId);
	    Map<String, Object> claims = new HashMap<>();
	    claims.put("subject", subject);
	    claims.put("roleId", roleId);
		return Jwts.builder()
				.setClaims(claims) // Set the map as the JWT claims
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)).signWith(getKey()).compact();
	}

	private static Key getKey() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));

	}
	public static boolean validateToken(String token) {

		try {
			Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token).getBody();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public static String getSubjectFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	public static String getIssuerFromToken(String token) {
		return getClaimFromToken(token, Claims::getIssuer);
	}

	public static Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public static <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token);
		return claimsResolver.apply(claims.getBody());
	}
	public static Claims getClaimFromToken(String jwtToken) {
		logger.debug("In getClaimFromToken "+jwtToken +" "+Jwts.parserBuilder()
        .setSigningKey(getKey())
        .build()
        .parseClaimsJws(jwtToken)
        .getBody());
		return Jwts.parserBuilder()
	            .setSigningKey(getKey())
	            .build()
	            .parseClaimsJws(jwtToken)
	            .getBody();
         
	    // Do something with subject and roleId...
	}
	public static void storeTokenInCookie(String token) {
		// Store the token on the client side (e.g., in a cookie)
		HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
				.getResponse();
		Cookie cookie = new Cookie(Constant.JWT_TOKEN_NAME, token);
		cookie.setSecure(true);
		cookie.setHttpOnly(true);		
		httpServletResponse.addCookie(cookie);
	}

	public static void removeTokenfromCookie(HttpServletResponse httpServletResponse) {
		// Store the token on the client side (e.g., in a cookie)
		Cookie c = new Cookie(Constant.JWT_TOKEN_NAME, null);
		c.setMaxAge(0);
		httpServletResponse.addCookie(c);

	}

}