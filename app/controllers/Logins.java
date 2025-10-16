package controllers;

import models.Usuario;
import play.mvc.Controller;

public class Logins extends Controller {
    
    // Exibe formulário de login
    public static void form() {
        render();
    }
    
    // Nova action: exibe formulário de cadastro
    public static void cadastro() {
        render();
    }
    
    // Processa o login
    public static void logar(String login, String senha) {
        if (login == null || login.trim().isEmpty() || 
            senha == null || senha.trim().isEmpty()) {
            flash.error("Por favor, preencha login e senha");
            form();
            return;
        }
        
        Usuario usuario = Usuario.autenticar(login, senha);
        
        if (usuario != null) {
            session.put("usuarioLogado", usuario.login);
            session.put("perfilUsuario", usuario.perfil.toString());
            session.put("nomeUsuario", usuario.nomeCompleto);
            
            flash.success("Bem-vindo, " + usuario.nomeCompleto + "!");
            Application.index();
        } else {
            flash.error("Login ou senha inválidos!");
            form();
        }
    }
    
    // Processa o cadastro de novo usuário
    public static void registrar(Usuario usuario) {
        // Validações
        if (usuario.nomeCompleto == null || usuario.nomeCompleto.trim().isEmpty()) {
            flash.error("Nome completo é obrigatório");
            cadastro();
            return;
        }
        
        if (usuario.email == null || usuario.email.trim().isEmpty()) {
            flash.error("Email é obrigatório");
            cadastro();
            return;
        }
        
        if (!usuario.email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            flash.error("Email inválido!");
            cadastro();
            return;
        }
        
        if (usuario.login == null || usuario.login.trim().isEmpty()) {
            flash.error("Login é obrigatório");
            cadastro();
            return;
        }
        
        if (usuario.senha == null || usuario.senha.trim().isEmpty()) {
            flash.error("Senha é obrigatória");
            cadastro();
            return;
        }
        
        if (usuario.perfil == null) {
            flash.error("Selecione um perfil de acesso");
            cadastro();
            return;
        }
        
        // Verifica se login já existe
        if (Usuario.loginExiste(usuario.login, null)) {
            flash.error("Este login já está cadastrado! Escolha outro.");
            cadastro();
            return;
        }
        
        try {
            usuario.status = models.Status.ATIVO;
            usuario.save();
            flash.success("Cadastro realizado com sucesso! Faça login para continuar.");
            form();
        } catch (Exception e) {
            flash.error("Erro ao cadastrar: " + e.getMessage());
            cadastro();
        }
    }
    
    public static void logout() {
        session.clear();
        flash.success("Você saiu do sistema!");
        form();
    }
}