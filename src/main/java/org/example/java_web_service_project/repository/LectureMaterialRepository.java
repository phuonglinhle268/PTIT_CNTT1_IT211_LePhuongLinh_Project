package org.example.java_web_service_project.repository;

import org.example.java_web_service_project.entity.LectureMaterial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LectureMaterialRepository extends JpaRepository<LectureMaterial, Long> {

    Page<LectureMaterial> findByCourse_Id(Long courseId, Pageable pageable);

    Page<LectureMaterial> findByUploadedBy_Id(Long uploadedById, Pageable pageable);
}
