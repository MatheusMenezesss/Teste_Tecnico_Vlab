package br.ufpe.tasktrack.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "lesson")
public class Lesson {

	public enum Status {
		DRAFT,
		PUBLISHED
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idLesson;

	@NotBlank
	@Size(min = 3)
	@Column(nullable = false)
	private String title;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status = Status.DRAFT;

	@Column(length = 2048)
	private String videoUrl;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "course_id", nullable = false)
	@JsonIgnore
	private Course course;

	public Lesson() {
	}

	public Lesson(String title, Status status, String videoUrl, Course course) {
		this.title = title;
		this.status = status == null ? Status.DRAFT : status;
		this.videoUrl = videoUrl;
		this.course = course;
	}

	@PrePersist
	public void definirStatusPadrao() {
		if (status == null) {
			status = Status.DRAFT;
		}
	}

	public Integer getIdLesson() {
		return idLesson;
	}

	public void setIdLesson(Integer idLesson) {
		this.idLesson = idLesson;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public void publicar() {
		this.status = Status.PUBLISHED;
	}

	public void voltarParaRascunho() {
		this.status = Status.DRAFT;
	}

	public void atualizarDados(String title, Status status, String videoUrl) {
		this.title = title;
		this.status = status == null ? Status.DRAFT : status;
		this.videoUrl = videoUrl;
	}

}
