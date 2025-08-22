package com.si.mindhealth.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

import org.hibernate.Hibernate;

@MappedSuperclass
@Getter @Setter
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, name = "created_at")
    private Date createdAt;

    @PrePersist
    private void setCA() {
        this.createdAt = new Date();
    }

    // Override để tránh bug so sánh lỗi khi dùng Set<>
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        BaseEntity that = (BaseEntity) o;
        return this.id != null && this.id.equals(that.id);
    }

    @Override
    public final int hashCode() {
        return (id != null) ? id.hashCode() : getClass().hashCode();
    }
}
