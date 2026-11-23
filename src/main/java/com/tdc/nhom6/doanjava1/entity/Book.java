package com.tdc.nhom6.doanjava1.entity;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tiêu đề không được để trống")
    @Column(nullable = false, length = 255)
    private String title;

    @NotBlank(message = "Tên tác giả không được để trống")
    @Column(nullable = false, length = 150)
    private String author;

    @Column(length = 100)
    private String category;

    @Column(length = 150)
    private String publisher;

    private Integer publishYear;

    @NotNull(message = "Số lượng bản không được để trống")
    @Min(value = 1, message = "Số lượng phải ít nhất là 1")
    @Column(nullable = false)
    private Integer totalCopies = 1;

    @Column(nullable = false)
    private Integer availableCopies = 1;

    @Column(length = 2000)
    private String description;

    @Lob
    @Column(name = "cover_image", columnDefinition = "LONGBLOB")
    private byte[] coverImage;

    @Column(name = "cover_type", length = 50)
    private String coverType;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    public Book() {}

    // ── Computed (not a DB column) ─────────────────────────────────────
    public boolean isHasCover() { return coverImage != null && coverImage.length > 0; }

    // ── Getters & Setters ─────────────────────────────────────────────
    public Long getId()                          { return id; }
    public void setId(Long id)                   { this.id = id; }
    public String getTitle()                     { return title; }
    public void setTitle(String t)               { this.title = t; }
    public String getAuthor()                    { return author; }
    public void setAuthor(String a)              { this.author = a; }
    public String getCategory()                  { return category; }
    public void setCategory(String c)            { this.category = c; }
    public String getPublisher()                 { return publisher; }
    public void setPublisher(String p)           { this.publisher = p; }
    public Integer getPublishYear()              { return publishYear; }
    public void setPublishYear(Integer y)        { this.publishYear = y; }
    public Integer getTotalCopies()              { return totalCopies; }
    public void setTotalCopies(Integer t)        { this.totalCopies = t; }
    public Integer getAvailableCopies()          { return availableCopies; }
    public void setAvailableCopies(Integer a)    { this.availableCopies = a; }
    public String getDescription()               { return description; }
    public void setDescription(String d)         { this.description = d; }
    public byte[] getCoverImage()                { return coverImage; }
    public void setCoverImage(byte[] c)          { this.coverImage = c; }
    public String getCoverType()                 { return coverType; }
    public void setCoverType(String t)           { this.coverType = t; }
    public LocalDateTime getCreatedAt()          { return createdAt; }
    public void setCreatedAt(LocalDateTime c)    { this.createdAt = c; }
}
