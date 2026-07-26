# Deployment Guide

## Prerequisites

### Server Requirements

- **OS**: Ubuntu 20.04+ or CentOS 8+
- **CPU**: 2+ cores
- **RAM**: 4GB minimum (8GB recommended)
- **Storage**: 50GB (SSD recommended)
- **Bandwidth**: 10 Mbps minimum

### Required Software

- PHP 8.3+
- MySQL 8.0+
- Nginx or Apache
- Node.js 18+
- Docker (optional)
- SSL Certificate (Let's Encrypt)

## Step 1: Server Setup

### Update System

```bash
sudo apt update && sudo apt upgrade -y
sudo apt install -y curl wget git zip unzip
```

### Install PHP

```bash
sudo apt install -y php8.3 php8.3-fpm php8.3-mysql php8.3-json php8.3-curl php8.3-bcmath php8.3-mbstring php8.3-xml php8.3-zip

# Verify installation
php -v
php -m | grep json
```

### Install MySQL

```bash
sudo apt install -y mysql-server-8.0

# Secure MySQL installation
sudo mysql_secure_installation

# Create database and user
mysql -u root -p

CREATE DATABASE transport_connect CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'transport_user'@'localhost' IDENTIFIED BY 'strong_password_here';
GRANT ALL PRIVILEGES ON transport_connect.* TO 'transport_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

### Install Nginx

```bash
sudo apt install -y nginx
sudo systemctl start nginx
sudo systemctl enable nginx

# Verify
sudo systemctl status nginx
```

### Install Node.js

```bash
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt install -y nodejs

# Verify
node -v
npm -v
```

## Step 2: Deploy Backend

### Clone Repository

```bash
cd /var/www
sudo git clone https://github.com/vinod2244/Transport-Connect.git
cd Transport-Connect/backend

# Set permissions
sudo chown -R www-data:www-data .
sudo chmod -R 755 .
```

### Install Dependencies

```bash
sudo -u www-data composer install --optimize-autoloader --no-dev
```

### Configure Environment

```bash
cp .env.example .env
sudo nano .env  # Edit with your settings

# Generate JWT Secret
openssl rand -base64 32

# Update in .env
JWT_SECRET=<generated_secret>
```

### Database Migration

```bash
mysql -u transport_user -p transport_connect < ../database/schema.sql
```

### Configure Nginx

```bash
sudo nano /etc/nginx/sites-available/default
```

```nginx
server {
    listen 80;
    server_name api.aptransportconnect.com;
    root /var/www/Transport-Connect/backend/public;
    index index.php;

    # Redirect HTTP to HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name api.aptransportconnect.com;
    root /var/www/Transport-Connect/backend/public;
    index index.php;

    # SSL Certificates
    ssl_certificate /etc/letsencrypt/live/api.aptransportconnect.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/api.aptransportconnect.com/privkey.pem;

    # SSL Configuration
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers on;

    # Security Headers
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;

    # PHP Configuration
    location ~ \.php$ {
        fastcgi_pass unix:/var/run/php/php8.3-fpm.sock;
        fastcgi_index index.php;
        fastcgi_param SCRIPT_FILENAME $document_root$fastcgi_script_name;
        include fastcgi_params;
    }

    # Routes
    location / {
        try_files $uri $uri/ /index.php?$query_string;
    }

    # Deny access to hidden files
    location ~ /\. {
        deny all;
    }
}
```

### Test Nginx

```bash
sudo nginx -t
sudo systemctl restart nginx
```

### Set Up SSL Certificate

```bash
sudo apt install -y certbot python3-certbot-nginx
sudo certbot certonly --nginx -d api.aptransportconnect.com
```

## Step 3: Deploy Admin Panel

### Build Admin Panel

```bash
cd /var/www/Transport-Connect/admin
npm install
npm run build

# Configure environment
echo "REACT_APP_API_URL=https://api.aptransportconnect.com/api" > .env.production
```

### Configure Nginx for Admin

```nginx
server {
    listen 443 ssl http2;
    server_name admin.aptransportconnect.com;
    root /var/www/Transport-Connect/admin/build;
    index index.html;

    ssl_certificate /etc/letsencrypt/live/admin.aptransportconnect.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/admin.aptransportconnect.com/privkey.pem;

    location / {
        try_files $uri /index.html;
    }
}
```

## Step 4: Database Backup

### Create Backup Script

```bash
#!/bin/bash
# /usr/local/bin/backup-db.sh

BACKUP_DIR="/backups/database"
DB_NAME="transport_connect"
DB_USER="transport_user"

mkdir -p $BACKUP_DIR

mysqldump -u $DB_USER -p$DB_PASSWORD $DB_NAME | gzip > $BACKUP_DIR/backup_$(date +%Y%m%d_%H%M%S).sql.gz

# Keep only last 30 days
find $BACKUP_DIR -name "*.gz" -mtime +30 -delete
```

### Schedule Backups

```bash
sudo crontab -e

# Add daily backup at 2 AM
0 2 * * * /usr/local/bin/backup-db.sh
```

## Step 5: Monitoring & Logging

### Set Up Logging

```bash
# Create log directory
sudo mkdir -p /var/www/Transport-Connect/backend/storage/logs
sudo chown -R www-data:www-data /var/www/Transport-Connect/backend/storage
```

### Monitor with Supervisor

```bash
sudo apt install -y supervisor

# Create supervisor config
sudo nano /etc/supervisor/conf.d/transport-api.conf
```

```ini
[program:transport-api]
command=/usr/bin/php -S 127.0.0.1:9000 -t /var/www/Transport-Connect/backend/public
process_name=%(program_name)s_%(process_num)02d
numprocs=4
autorestart=true
user=www-data
stdout_logfile=/var/log/transport-api.log
stderr_logfile=/var/log/transport-api-error.log
```

## Step 6: Firewall Configuration

```bash
sudo ufw enable
sudo ufw allow 22/tcp      # SSH
sudo ufw allow 80/tcp      # HTTP
sudo ufw allow 443/tcp     # HTTPS
sudo ufw allow 3306/tcp    # MySQL (from app only)
sudo ufw status
```

## Step 7: Performance Optimization

### PHP Configuration

```bash
sudo nano /etc/php/8.3/fpm/php.ini
```

```ini
max_execution_time = 300
max_input_time = 300
memory_limit = 512M
upload_max_filesize = 100M
post_max_size = 100M
```

### MySQL Configuration

```bash
sudo nano /etc/mysql/mysql.conf.d/mysqld.cnf
```

```ini
max_connections = 200
max_allowed_packet = 256M
default_storage_engine = InnoDB
innodb_buffer_pool_size = 1G
innodb_log_file_size = 256M
```

### Nginx Caching

```nginx
# Add to nginx config
proxy_cache_path /var/cache/nginx levels=1:2 keys_zone=api_cache:10m max_size=1g inactive=60m;

location ~* ^/api/(vehicles|vehicle-types)/ {
    proxy_cache api_cache;
    proxy_cache_valid 200 10m;
    add_header X-Cache-Status $upstream_cache_status;
}
```

## Step 8: Docker Deployment (Optional)

### Create Dockerfile

```dockerfile
FROM php:8.3-fpm

RUN apt-get update && apt-get install -y \
    mysql-client \
    git \
    && docker-php-ext-install pdo pdo_mysql

WORKDIR /app

COPY backend/ .

RUN curl -sS https://getcomposer.org/installer | php -- --install-dir=/usr/local/bin --filename=composer
RUN composer install

EXPOSE 9000

CMD ["php-fpm"]
```

### Docker Compose

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_DATABASE: transport_connect
      MYSQL_USER: transport_user
      MYSQL_PASSWORD: strong_password
      MYSQL_ROOT_PASSWORD: root_password
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

  php:
    build: .
    ports:
      - "9000:9000"
    depends_on:
      - mysql
    volumes:
      - ./backend:/app

  nginx:
    image: nginx:latest
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf
    depends_on:
      - php

volumes:
  mysql_data:
```

## Step 9: Post-Deployment Checks

```bash
# Test API health
curl https://api.aptransportconnect.com/api/health

# Check services
sudo systemctl status nginx
sudo systemctl status php8.3-fpm
sudo systemctl status mysql

# Check logs
tail -f /var/www/Transport-Connect/backend/storage/logs/app.log
tail -f /var/log/nginx/error.log
```

## Step 10: Maintenance

### Regular Updates

```bash
# Update packages
sudo apt update && sudo apt upgrade -y

# Update PHP dependencies
cd /var/www/Transport-Connect/backend
composer update

# Update Node dependencies
cd /var/www/Transport-Connect/admin
npm update
```

### Monitoring Health

```bash
# Monitor disk usage
df -h

# Monitor memory
free -h

# Monitor processes
top

# Check database size
mysql -u transport_user -p -e "SELECT SUM(data_length + index_length) / 1024 / 1024 / 1024 AS 'Size (GB)' FROM information_schema.TABLES WHERE table_schema='transport_connect';"
```

## Troubleshooting

### API not responding

```bash
# Check PHP-FPM
sudo systemctl status php8.3-fpm
sudo systemctl restart php8.3-fpm

# Check Nginx
sudo nginx -t
sudo systemctl restart nginx
```

### Database connection error

```bash
# Check MySQL
sudo systemctl status mysql

# Test connection
mysql -u transport_user -p -h localhost

# Check .env file
cat /var/www/Transport-Connect/backend/.env | grep DB_
```

### High server load

```bash
# Check running processes
top

# Check slow queries
mysql -u root -p -e "SET GLOBAL slow_query_log = 'ON';"

# Monitor in real-time
mysql -u root -p -e "SHOW FULL PROCESSLIST;"
```

## Support

For deployment assistance: deploy-support@aptransportconnect.com
