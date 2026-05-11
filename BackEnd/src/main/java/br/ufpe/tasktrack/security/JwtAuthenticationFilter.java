/* Intercepta requests, valida o JWT e popula o contexto de autenticacao do Spring. */
package br.ufpe.tasktrack.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1) Lê o header Authorization (onde vem o token JWT)
        // Exemplo esperado: "Authorization: Bearer eyJhbGciOi..."
        final String authHeader = request.getHeader("Authorization");

        // 2) Se NÃO existe token, não tentamos autenticar.
        // Apenas deixamos a request seguir.
        // Se a rota for protegida, o Spring Security vai negar depois (401/403).
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3) Remove o prefixo "Bearer " e fica só com o token
        final String token = authHeader.substring(7);

        try {
            // 4) Valida assinatura e expiração do JWT.
            // Se não for válido, não autentica e deixa seguir.
            // (Rotas protegidas serão negadas pelo Spring)
            if (!jwtTokenProvider.validarToken(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            // 5) Extrai o email do token (normalmente do "sub")
            final String email = jwtTokenProvider.extrairEmail(token);

            // 6) Só autentica se:
            // - conseguiu extrair um email
            // - e ainda NÃO existe autenticação no contexto (evita sobrescrever)
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 7) Cria um Authentication que representa "usuário autenticado".
                // Aqui estamos usando o próprio email como principal (mais simples).
                // Authorities vazias por enquanto (sem roles).
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email, // principal
                                null,  // credentials (não precisamos guardar senha)
                                Collections.emptyList() // roles/authorities
                        );

                // 8) Anexa detalhes da request (IP, session, etc.)
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 9) Salva a autenticação no contexto do Spring Security
                // A partir daqui, a request passa a ser "autenticada"
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            // 10) Continua a cadeia de filtros (sempre chamar isso)
            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            // 11) Se o token vier malformado e der exception aqui, não derruba o servidor.
            // Apenas segue sem autenticar; rotas protegidas serão negadas.
            filterChain.doFilter(request, response);
        }
    }

    private String extrairToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }

        return null;
    }
}