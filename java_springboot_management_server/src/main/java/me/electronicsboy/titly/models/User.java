package me.electronicsboy.titly.models;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import me.electronicsboy.titly.data.PrivilegeLevel;

@Table(name = "users")
@Entity
public class User implements UserDetails {
    /**
	 * 
	 */
	private static final long serialVersionUID = -7895442402116834784L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
	@SequenceGenerator(name = "user_seq", sequenceName = "user_sequence", allocationSize = 1)
	@Column(nullable = false)
    private long id;

    @Column(nullable = true)
    private String fullname;
    
    @Column(nullable = false, unique = true)
    private String username;

    @Column(length = 256, nullable = true, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private PrivilegeLevel privilegeLevel;
    
    @Column(nullable = false)
    private boolean enabled;
    
    @Column(nullable = false)
    private String location;
    
    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;
    
	public User setId(long id) {
		this.id = id;
		return this;
	}

	public User setFullname(String fullname) {
		this.fullname = fullname;
		return this;
	}

	public User setEmail(String email) {
		this.email = email;
		return this;
	}

	public User setPassword(String password) {
		this.password = password;
		return this;
	}

	public User setPrivilageLevel(PrivilegeLevel privilageLevel) {
		this.privilegeLevel = privilageLevel;
		return this;
	}

	public User setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
		return this;
	}

	public User setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
		return this;
	}
	
	public User setUsername(String username) {
		this.username = username;
		return this;
	}

	public long getId() {
		return id;
	}

	public String getFullname() {
		return fullname;
	}

	public String getEmail() {
		return email;
	}

	public PrivilegeLevel getPrivilegeLevel() {
		return privilegeLevel;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}
	
	@Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

	@Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    public User setEnabled(boolean enabled) {
    	this.enabled = enabled;
    	return this;
    }
    
    public User setLocation(String location) {
    	this.location = location;
    	return this;
    }
    
    public String getLocation() {
    	return this.location;
    }
}
