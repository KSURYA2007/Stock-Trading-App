package stocktrading;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class User {
    private final int userId;
    private String name;
    private String email;
    private String phone;
    private Role role;
    private String passwordSalt;
    private String passwordHash;

    public User(int userId, String name, String email, String phone, Role role, char[] password) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        setPassword(password);
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public final void setPassword(char[] password) {
        if (password == null) {
            throw new IllegalArgumentException("Password is required.");
        }
        try {
            if (password.length < 6) {
                throw new IllegalArgumentException("Password must contain at least 6 characters.");
            }
            byte[] salt = new byte[16];
            new SecureRandom().nextBytes(salt);
            passwordSalt = Base64.getEncoder().encodeToString(salt);
            passwordHash = hash(password, salt);
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    public boolean verifyPassword(char[] password) {
        if (password == null) {
            return false;
        }
        try {
            byte[] salt = Base64.getDecoder().decode(passwordSalt);
            String candidate = hash(password, salt);
            return java.security.MessageDigest.isEqual(
                    candidate.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                    passwordHash.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    private static String hash(char[] password, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, 120_000, 256);
        try {
            byte[] encoded = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(encoded);
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Could not securely process the password.", exception);
        } finally {
            spec.clearPassword();
        }
    }
}
