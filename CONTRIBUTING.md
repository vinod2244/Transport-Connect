# Contributing Guide

## Welcome to AP Transport Connect!

Thank you for your interest in contributing to AP Transport Connect. This guide will help you understand our development process and coding standards.

---

## 🏗️ Architecture Overview Before Contributing

Please familiarize yourself with:
- **Backend**: [ARCHITECTURE.md](docs/ARCHITECTURE.md)
- **Database**: [DATABASE.md](docs/DATABASE.md)
- **API**: [API.md](docs/API.md)
- **Coding Standards**: [CODING_STANDARDS.md](docs/CODING_STANDARDS.md)

---

## 📋 Before Starting

### Setup Development Environment

```bash
# Clone repository
git clone https://github.com/vinod2244/Transport-Connect.git
cd Transport-Connect

# Follow setup guide
cat docs/SETUP.md
```

### Create a Feature Branch

```bash
git checkout -b feature/your-feature-name
# or
git checkout -b fix/your-bug-fix
```

---

## ✅ Code Quality Standards

### Backend (PHP)

```bash
# Install dependencies
composer install

# Run tests
php artisan test

# Code formatting
php artisan pint

# Static analysis
php artisan tinker
```

### Android (Kotlin)

```bash
# Build
./gradlew build

# Run tests
./gradlew test

# Lint
./gradlew lint
```

### Admin Panel (Vue.js)

```bash
# Install dependencies
npm install

# Run tests
npm run test

# Lint
npm run lint

# Build
npm run build
```

---

## 📝 Commit Message Format

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types
- **feat**: New feature
- **fix**: Bug fix
- **docs**: Documentation changes
- **style**: Code style changes
- **refactor**: Code refactoring
- **test**: Test additions/modifications
- **chore**: Build or dependency updates

### Example

```
feat(booking): Add multi-stop booking support

Implement ability to add multiple stops to a single booking.
Users can now specify intermediate stops that drivers must visit.

Closes #123
```

---

## 🔄 Pull Request Process

### Before Submitting

1. **Update from main**
   ```bash
   git fetch origin
   git rebase origin/main
   ```

2. **Run Tests**
   ```bash
   # Backend
   php artisan test
   
   # Android
   ./gradlew test
   
   # Admin
   npm run test
   ```

3. **Code Review Checks**
   - [ ] Follows coding standards
   - [ ] Tests included
   - [ ] Documentation updated
   - [ ] No console errors/warnings
   - [ ] Performance optimized

### PR Description Template

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Related Issues
Closes #(issue number)

## Testing
Describe testing performed

## Screenshots (if applicable)
Add screenshots for UI changes

## Checklist
- [ ] Tests pass
- [ ] Code follows style guidelines
- [ ] Documentation updated
- [ ] No new warnings generated
```

---

## 🐛 Reporting Bugs

### Issue Template

```markdown
## Description
Clear description of the bug

## Steps to Reproduce
1. Step 1
2. Step 2
3. Step 3

## Expected Behavior
What should happen

## Actual Behavior
What actually happens

## Environment
- Component: Backend/Android/Admin
- Version: X.X.X
- OS: (if applicable)

## Screenshots/Logs
Attach relevant error logs or screenshots
```

---

## 📚 Documentation Guidelines

### Code Comments

```php
// Bad
$result = $service->calculate($data); // calculate result

// Good
// Calculate pricing based on distance and time multipliers
$result = $service->calculateBookingPrice($data);
```

### API Documentation

```php
/**
 * Calculate booking price
 *
 * @param array $data Booking data (distance_km, duration_minutes)
 * @return float Calculated price
 * @throws InvalidArgumentException If data is invalid
 */
public function calculateBookingPrice(array $data): float
```

### README Updates

- Update docs if adding/changing features
- Add section to [FOLDER_STRUCTURE.md](FOLDER_STRUCTURE.md) if creating new modules
- Update [API.md](docs/API.md) for API changes

---

## 🔐 Security Considerations

### Before Committing

- [ ] No API keys or secrets in code
- [ ] No hardcoded credentials
- [ ] Input validation implemented
- [ ] SQL injection protection (use ORM)
- [ ] XSS protection (escape output)
- [ ] CSRF tokens used
- [ ] Password properly hashed

---

## 📊 Performance Guidelines

### Backend
- Database queries optimized
- N+1 queries prevented
- Caching implemented where appropriate
- Response time < 200ms

### Android
- No ANR (Application Not Responding)
- Smooth 60 FPS animations
- Optimized images and resources
- Proper memory management

### Admin Panel
- Bundle size optimized
- Lazy loading implemented
- Images compressed
- Load time < 2s

---

## 🚀 Release Checklist

- [ ] Version bumped
- [ ] CHANGELOG updated
- [ ] All tests passing
- [ ] Performance verified
- [ ] Security audit completed
- [ ] Documentation reviewed
- [ ] All issues resolved
- [ ] No breaking changes

---

## 📞 Need Help?

- Check [TROUBLESHOOTING.md](docs/TROUBLESHOOTING.md)
- Review existing issues
- Ask in discussions section
- Contact maintainers

---

## 🎓 Learning Resources

- [Laravel Documentation](https://laravel.com/docs)
- [Android Development Guide](https://developer.android.com/)
- [Kotlin Documentation](https://kotlinlang.org/docs/)
- [Vue.js Guide](https://vuejs.org/guide/)

---

Thank you for contributing! 🙏
