package com.ss.jwt.util;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.ss.common.util.Constant;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

public class JwtUtil {

	private static final String SECRET_KEY = "daf66e01593f61a15b857cf433aae03a005812b31234e149036bcc8dee755dbb";
	private static final long EXPIRATION_TIME = (5*60*60*1000); // 5 hours

	public static String generateToken(String subject) {
		return Jwts.builder().setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)).signWith(getKey()).compact();
	}

	private static Key getKey() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
		
	}

	public String getSubject(String token) {
		Claims claims = Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token).getBody();

		return claims.getSubject();
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

	public String getUserNameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	public String getIssuerFromToken(String token) {
		return getClaimFromToken(token, Claims::getIssuer);
	}

	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token);
		return claimsResolver.apply(claims.getBody());
	}

	public static void storeTokenAtClientSide(String token) {
		// Store the token on the client side (e.g., in a cookie)
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
				.getResponse();
		response.addCookie(new Cookie(Constant.JWT_TOKEN_NAME, token));
	}

	public static void removeTokenAtClientSide() {
		// Store the token on the client side (e.g., in a cookie)
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
				.getResponse();
		Cookie c = new Cookie(Constant.JWT_TOKEN_NAME, null);
		c.setMaxAge(0);
		response.addCookie(c);

	}

}