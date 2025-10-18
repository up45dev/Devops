# 🔧 **CONFIGURAÇÃO OAUTH2 - SETUP GUIDE**

## 🔐 **Configuração de Provedores OAuth2**

Para utilizar a autenticação OAuth2 com provedores externos, você precisa configurar as credenciais nos serviços.

---

## 🌐 **Google OAuth2 Setup**

### **1. Criar Projeto no Google Cloud Console**
1. Acesse: https://console.cloud.google.com/
2. Crie um novo projeto ou selecione um existente
3. Vá para **APIs & Services > Credentials**
4. Clique em **Create Credentials > OAuth 2.0 Client IDs**

### **2. Configurar OAuth2 Client**
- **Application type:** Web application
- **Name:** Task Management System
- **Authorized redirect URIs:**
  ```
  http://localhost:8080/oauth2/callback/google
  http://localhost:8080/login/oauth2/code/google
  ```

### **3. Obter Credenciais**
Após criar, você receberá:
- **Client ID:** `your-google-client-id`
- **Client Secret:** `your-google-client-secret`

---

## 🐱 **GitHub OAuth2 Setup**

### **1. Criar OAuth App no GitHub**
1. Acesse: https://github.com/settings/developers
2. Clique em **New OAuth App**
3. Preencha os dados:
   - **Application name:** Task Management System
   - **Homepage URL:** `http://localhost:8091`
   - **Authorization callback URL:** `http://localhost:8080/oauth2/callback/github`

### **2. Obter Credenciais**
Após criar, você receberá:
- **Client ID:** `your-github-client-id`
- **Client Secret:** `your-github-client-secret`

---

## ⚙️ **Configuração no Sistema**

### **Método 1: Variáveis de Ambiente (Recomendado)**
```bash
export GOOGLE_CLIENT_ID="your-google-client-id"
export GOOGLE_CLIENT_SECRET="your-google-client-secret"
export GITHUB_CLIENT_ID="your-github-client-id"
export GITHUB_CLIENT_SECRET="your-github-client-secret"
export JWT_SECRET="your-super-secret-jwt-key-with-minimum-256-bits"
```

### **Método 2: Arquivo de Configuração**
Edite `auth-service/src/main/resources/application.yml`:

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: "your-google-client-id"
            client-secret: "your-google-client-secret"
          github:
            client-id: "your-github-client-id"
            client-secret: "your-github-client-secret"

jwt:
  secret: "your-super-secret-jwt-key-with-minimum-256-bits"
```

---

## 🔑 **Geração de JWT Secret**

Para gerar uma chave JWT segura:

```bash
# Opção 1: OpenSSL
openssl rand -base64 64

# Opção 2: Node.js
node -e "console.log(require('crypto').randomBytes(64).toString('base64'))"

# Opção 3: Python
python3 -c "import secrets; print(secrets.token_urlsafe(64))"
```

---

## 🚀 **URLs de Teste**

### **Local Development**
- **Login Page:** http://localhost:8080/oauth2/authorize/google
- **GitHub Login:** http://localhost:8080/oauth2/authorize/github
- **Admin UI:** http://localhost:8090

### **Fluxo de Autenticação**
1. **Usuário acessa:** Admin UI (http://localhost:8090)
2. **Redirecionamento:** Auth Service para login
3. **OAuth2 Flow:** Provedor externo (Google/GitHub)
4. **Callback:** Auth Service processa e gera JWT
5. **Redirecionamento:** De volta para Admin UI com token

---

## 🛠️ **Configuração de Desenvolvimento**

### **1. Arquivo `.env` (Opcional)**
Crie na raiz do projeto:
```bash
# OAuth2 Configuration
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
GITHUB_CLIENT_ID=your-github-client-id
GITHUB_CLIENT_SECRET=your-github-client-secret

# JWT Configuration
JWT_SECRET=your-super-secret-jwt-key-with-minimum-256-bits

# Database Configuration (Optional)
DB_HOST=localhost
DB_PORT=5432
DB_NAME=taskmanagement
DB_USER=admin
DB_PASSWORD=admin123
```

### **2. Script de Setup Automatizado**
```bash
#!/bin/bash
# setup-oauth.sh

echo "🔧 Configurando OAuth2 para Task Management..."

read -p "Google Client ID: " GOOGLE_CLIENT_ID
read -p "Google Client Secret: " GOOGLE_CLIENT_SECRET
read -p "GitHub Client ID: " GITHUB_CLIENT_ID
read -p "GitHub Client Secret: " GITHUB_CLIENT_SECRET

# Gerar JWT Secret automaticamente
JWT_SECRET=$(openssl rand -base64 64)

# Exportar variáveis
export GOOGLE_CLIENT_ID
export GOOGLE_CLIENT_SECRET
export GITHUB_CLIENT_ID
export GITHUB_CLIENT_SECRET
export JWT_SECRET

echo "✅ Configuração OAuth2 concluída!"
echo "🔑 JWT Secret gerado automaticamente"
echo "🚀 Agora você pode executar: ./start-services.sh"
```

---

## 🧪 **Testando a Configuração**

### **1. Verificar Auth Service**
```bash
curl -X GET http://localhost:8080/actuator/health
```

### **2. Testar Login Local**
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "admin",
    "password": "admin123"
  }'
```

### **3. Testar OAuth2 Endpoints**
- **Google:** http://localhost:8080/oauth2/authorization/google
- **GitHub:** http://localhost:8080/oauth2/authorization/github

---

## 🔍 **Troubleshooting**

### **Erro: "redirect_uri_mismatch"**
- Verifique se as URLs de callback estão corretas nos provedores
- Certifique-se de que as portas estão corretas (8080 para auth-service)

### **Erro: "invalid_client"**
- Verifique se Client ID e Client Secret estão corretos
- Confirme se as variáveis de ambiente estão carregadas

### **Erro: "JWT signature does not match"**
- Verifique se JWT_SECRET é o mesmo em todos os serviços
- Certifique-se de que o secret tem pelo menos 256 bits

### **Problemas de CORS**
- Verifique se o API Gateway está configurado corretamente
- Confirme se as URLs permitidas incluem localhost

---

## 📚 **Recursos Adicionais**

- **Spring Security OAuth2:** https://docs.spring.io/spring-security/reference/servlet/oauth2/
- **Google OAuth2:** https://developers.google.com/identity/protocols/oauth2
- **GitHub OAuth:** https://docs.github.com/en/developers/apps/building-oauth-apps
- **JWT Best Practices:** https://tools.ietf.org/html/rfc7519

---

## ✅ **Checklist de Configuração**

- [ ] Criar projeto no Google Cloud Console
- [ ] Configurar OAuth2 Client no Google
- [ ] Criar OAuth App no GitHub
- [ ] Configurar variáveis de ambiente
- [ ] Gerar JWT Secret seguro
- [ ] Testar endpoints de autenticação
- [ ] Verificar redirecionamentos OAuth2
- [ ] Confirmar integração com API Gateway

**🎉 Após completar este checklist, sua autenticação OAuth2 estará totalmente funcional!**