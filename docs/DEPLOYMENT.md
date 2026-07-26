# AP Transport Connect - Deployment Guide

## 🚀 Production Deployment

### Pre-Deployment Checklist

- [ ] Code reviewed and tested
- [ ] Environment variables configured
- [ ] Database backups scheduled
- [ ] SSL certificates installed
- [ ] CDN configured
- [ ] Monitoring tools set up
- [ ] Error tracking configured
- [ ] Load balancer configured
- [ ] Security audit completed
- [ ] Performance tested

---

## 🖥️ Server Setup

### 1. Server Requirements

**OS**: Ubuntu 22.04 LTS or CentOS 8+

**Minimum Specs**:
- CPU: 4 cores
- RAM: 8GB
- Storage: 50GB SSD
- Bandwidth: 10Mbps minimum

### 2. Initial Server Setup

```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Install dependencies
sudo apt install -y curl wget git vim htop net-tools

# Install PHP 8.3
sudo apt install -y php8.3 php8.3-fpm php8.3-cli php8.3-mysql \
  php8.3-redis php8.3-curl php8.3-json php8.3-bcmath \
  php8.3-gd php8.3-xml php8.3-zip php8.3-mbstring

# Install MySQL 8
sudo apt install -y mysql-server mysql-client

# Install Redis
sudo apt install -y redis-server

# Install Nginx
sudo apt install -y nginx

# Install Supervisor
sudo apt install -y supervisor

# Install Node.js (for admin panel)
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt install -y nodejs

# Install Composer
curl -sS https://getcomposer.org/installer | php
sudo mv composer.phar /usr/local/bin/composer
composer --version
```

### 3. Firewall Setup

```bash
# Enable UFW
sudo ufw enable

# Allow SSH
sudo ufw allow 22/tcp

# Allow HTTP
sudo ufw allow 80/tcp

# Allow HTTPS
sudo ufw allow 443/tcp

# Allow MySQL (internal only)
sudo ufw allow from 192.168.1.0/24 to any port 3306

# Allow Redis (internal only)
sudo ufw allow from 192.168.1.0/24 to any port 6379

# Verify rules
sudo ufw status
```

### 4. SSH Key Setup

```bash
# Generate SSH key
ssh-keygen -t rsa -b 4096 -f ~/.ssh/deployment_key

# Copy public key
cat ~/.ssh/deployment_key.pub

# Add to GitHub Deploy Keys
# Settings -> Deploy Keys -> Add new
```

---

## 🗄️ Database Setup

### 1. MySQL Configuration

```bash
# Secure MySQL
sudo mysql_secure_installation

# Create production database
mysql -u root -p
CREATE DATABASE ap_transport_connect CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'app_user'@'localhost' IDENTIFIED BY 'strong_password';
GRANT ALL PRIVILEGES ON ap_transport_connect.* TO 'app_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

### 2. Enable MySQL Binary Logging

```bash
sudo nano /etc/mysql/mysql.conf.d/mysqld.cnf
```

Add:
```ini
[mysqld]
log_bin = /var/log/mysql/mysql-bin.log
server-id = 1
binlog_format = ROW
```

Restart MySQL:
```bash
sudo systemctl restart mysql
```

### 3. Backup Strategy

```bash
# Create backup script
sudo nano /usr/local/bin/backup-db.sh
```

```bash
#!/bin/bash
BACKUP_DIR="/var/backups/mysql"
DATE=$(date +%Y%m%d_%H%M%S)

mysqldump -u app_user -p'strong_password' ap_transport_connect \
  | gzip > $BACKUP_DIR/ap_transport_connect_$DATE.sql.gz

# Keep only last 7 days
find $BACKUP_DIR -type f -mtime +7 -delete
```

Make executable:
```bash
sudo chmod +x /usr/local/bin/backup-db.sh
```

Schedule with cron:
```bash
sudo crontab -e
```

Add:
```cron
0 2 * * * /usr/local/bin/backup-db.sh >> /var/log/db-backup.log 2>&1
```

---

## 🔐 SSL/TLS Setup

### Let's Encrypt with Certbot

```bash
# Install Certbot
sudo apt install -y certbot python3-certbot-nginx

# Generate certificate
sudo certbot certonly --nginx -d api.aptransportconnect.com -d aptransportconnect.com

# Auto-renewal
sudo systemctl enable certbot.timer
sudo systemctl start certbot.timer

# Test renewal
sudo certbot renew --dry-run
```

---

## 🌐 Nginx Configuration

### 1. Backend API Configuration

```bash
sudo nano /etc/nginx/sites-available/api.aptransportconnect.com
```

```nginx
upstream api_backend {
    server 127.0.0.1:8000;
    server 127.0.0.1:8001;
    server 127.0.0.1:8002;
}

server {
    listen 80;
    server_name api.aptransportconnect.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name api.aptransportconnect.com;

    ssl_certificate /etc/letsencrypt/live/api.aptransportconnect.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/api.aptransportconnect.com/privkey.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers on;

    client_max_body_size 50M;
    proxy_connect_timeout 600s;
    proxy_send_timeout 600s;
    proxy_read_timeout 600s;

    access_log /var/log/nginx/api.access.log;
    error_log /var/log/nginx/api.error.log;

    # CORS Headers
    add_header 'Access-Control-Allow-Origin' '*' always;
    add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, DELETE, PATCH, OPTIONS' always;
    add_header 'Access-Control-Allow-Headers' 'DNT,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Range,Authorization' always;

    # Security Headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Referrer-Policy "no-referrer-when-downgrade" always;

    # Rate limiting
    limit_req_zone $binary_remote_addr zone=api_limit:10m rate=100r/m;
    limit_req zone=api_limit burst=200 nodelay;

    location / {
        proxy_pass http://api_backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_buffering off;
    }

    location ~ /\.(?!well-known) {
        deny all;
    }
}
```

Enable site:
```bash
sudo ln -s /etc/nginx/sites-available/api.aptransportconnect.com /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

### 2. Admin Panel Configuration

```bash
sudo nano /etc/nginx/sites-available/admin.aptransportconnect.com
```

```nginx
server {
    listen 80;
    server_name admin.aptransportconnect.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name admin.aptransportconnect.com;

    ssl_certificate /etc/letsencrypt/live/admin.aptransportconnect.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/admin.aptransportconnect.com/privkey.pem;

    root /var/www/admin/dist;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
```

---

## 🚀 Backend Deployment

### 1. Clone & Setup Application

```bash
cd /var/www
sudo git clone https://github.com/vinod2244/Transport-Connect.git
cd Transport-Connect/backend

# Setup permissions
sudo chown -R www-data:www-data .
sudo chmod -R 755 .
sudo chmod -R 775 storage bootstrap/cache

# Install dependencies
composer install --no-dev --optimize-autoloader

# Copy env file
cp .env.example .env

# Configure .env
sudo nano .env
```

### 2. Generate Application Key

```bash
php artisan key:generate
php artisan jwt:secret
```

### 3. Database Migration

```bash
php artisan migrate --force
php artisan db:seed --class=RoleSeeder
php artisan db:seed --class=AdminSeeder
```

### 4. Cache Configuration

```bash
php artisan config:cache
php artisan route:cache
php artisan view:cache
```

### 5. PHP-FPM Configuration

```bash
sudo nano /etc/php/8.3/fpm/pool.d/www.conf
```

Update:
```ini
pm = dynamic
pm.max_children = 20
pm.start_servers = 10
pm.min_spare_servers = 5
pm.max_spare_servers = 15
pm.max_requests = 1000
```

Restart PHP-FPM:
```bash
sudo systemctl restart php8.3-fpm
```

### 6. Queue Configuration (Supervisor)

```bash
sudo nano /etc/supervisor/conf.d/transport-queue.conf
```

```ini
[program:transport-queue]
process_name=%(program_name)s_%(process_num)02d
command=php /var/www/Transport-Connect/backend/artisan queue:work redis --sleep=3 --tries=3
autostart=true
autorestart=true
numprocs=4
redirect_stderr=true
stdout_logfile=/var/log/queue.log
user=www-data
```

Update Supervisor:
```bash
sudo supervisorctl reread
sudo supervisorctl update
sudo supervisorctl start transport-queue:*
```

---

## 📱 Admin Panel Deployment

### 1. Build & Deploy

```bash
cd /var/www/Transport-Connect/admin

# Install dependencies
npm install --production

# Build
npm run build

# Deploy
sudo rm -rf /var/www/admin
sudo mkdir -p /var/www/admin
sudo cp -r dist/* /var/www/admin/
sudo chown -R www-data:www-data /var/www/admin
```

---

## 📊 Monitoring & Logging

### 1. Application Monitoring

```bash
# Install New Relic
sudo apt install newrelic-php5
sudo newrelic-install install

# Configure
sudo nano /etc/php/8.3/mods-available/newrelic.ini
```

### 2. Log Management

```bash
# Install ELK Stack or use external service
# Configure Laravel to use Stackdriver

sudo nano /var/www/Transport-Connect/backend/.env
# LOG_CHANNEL=stackdriver
```

### 3. Uptime Monitoring

```bash
# Health check endpoint
curl -X GET https://api.aptransportconnect.com/api/v1/health
```

---

## 🔄 CI/CD Pipeline

### GitHub Actions Workflow

```yaml
# .github/workflows/deploy.yml
name: Deploy to Production

on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Deploy Backend
        run: |
          ssh -i ${{ secrets.DEPLOY_KEY }} user@server \
            'cd /var/www/Transport-Connect && git pull && \
            composer install && php artisan migrate'
      
      - name: Deploy Admin
        run: |
          ssh -i ${{ secrets.DEPLOY_KEY }} user@server \
            'cd /var/www/Transport-Connect/admin && \
            npm install && npm run build'
```

---

## 🔧 Maintenance

### Regular Tasks

```bash
# Weekly
0 0 * * 0 php /var/www/Transport-Connect/backend/artisan queue:restart

# Daily
0 1 * * * php /var/www/Transport-Connect/backend/artisan schedule:run

# Backup
0 2 * * * /usr/local/bin/backup-db.sh

# Log cleanup
0 3 * * * find /var/log -name "*.log" -mtime +30 -delete
```

### Update Server

```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Update PHP extensions
sudo apt upgrade -y php8.3-*

# Update Composer
composer self-update

# Update npm packages
npm update -g
```

---

## 📈 Performance Optimization

### 1. Redis Caching

```php
// app/config/cache.php
'default' => env('CACHE_DRIVER', 'redis'),
'redis' => [
    'client' => 'predis',
    'connection' => 'cache',
],
```

### 2. Database Optimization

```bash
# Analyze tables
mysql -u root -p -e "ANALYZE TABLE ap_transport_connect.*"

# Optimize tables
mysql -u root -p -e "OPTIMIZE TABLE ap_transport_connect.*"
```

### 3. CDN Configuration

Use CloudFlare or AWS CloudFront for:
- Static assets (CSS, JS)
- Images
- API responses (if cacheable)

---

## 🚨 Rollback Procedure

```bash
# Check git history
git log --oneline -10

# Rollback
git revert <commit_hash>
git push origin main

# Run migrations rollback if needed
php artisan migrate:rollback

# Clear caches
php artisan cache:clear
php artisan config:clear
```

---

**Last Updated**: 2024
