package com.tdc.nhom6.doanjava1.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "digital_documents")
public class DigitalDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tiêu đề không được để trống")
    @Column(nullable = false, length = 255)
    private String title;

    @Column(length = 150)
    private String author;

    @Column(length = 2000)
    private String description;

    @Column(length = 20)
    private String documentType = "OTHER"; // THESIS, TEXTBOOK, OTHER

    @Column(nullable = false, length = 255)
    private String fileName;

    @Lob
    @Column(name = "file_data", columnDefinition = "LONGBLOB")
    private byte[] fileData;

    @Column(length = 100)
    private String contentType;

    private Long fileSize;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private Account uploadedBy;

    @Column(updatable = false)
    private LocalDateTime uploadedAt;

    @Column(nullable = false)
    private Long downloadCount = 0L;

    @PrePersist
    protected void onCreate() { this.uploadedAt = LocalDateTime.now(); }

    public DigitalDocument() {}

    // ── Getters & Setters ─────────────────────────────────────────────
    public Long getId()                          { return id; }
    public void setId(Long id)                   { this.id = id; }
    public String getTitle()                     { return title; }
    public void setTitle(String t)               { this.title = t; }
    public String getAuthor()                    { return author; }
    public void setAuthor(String a)              { this.author = a; }
    public String getDescription()               { return description; }
    public void setDescription(String d)         { this.description = d; }
    public String getDocumentType()              { return documentType; }
    public void setDocumentType(String dt)       { this.documentType = dt; }
    public String getFileName()                  { return fileName; }
    public void setFileName(String f)            { this.fileName = f; }
    public byte[] getFileData()                  { return fileData; }
    public void setFileData(byte[] f)            { this.fileData = f; }
    public String getContentType()               { return contentType; }
    public void setContentType(String c)         { this.contentType = c; }
    public Long getFileSize()                    { return fileSize; }
    public void setFileSize(Long s)              { this.fileSize = s; }
    public Account getUploadedBy()               { return uploadedBy; }
    public void setUploadedBy(Account u)         { this.uploadedBy = u; }
    public LocalDateTime getUploadedAt()         { return uploadedAt; }
    public void setUploadedAt(LocalDateTime t)   { this.uploadedAt = t; }
    public Long getDownloadCount()               { return downloadCount; }
    public void setDownloadCount(Long c)         { this.downloadCount = c; }
}
