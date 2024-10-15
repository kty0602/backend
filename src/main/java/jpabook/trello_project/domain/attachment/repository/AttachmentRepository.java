package jpabook.trello_project.domain.attachment.repository;

import jpabook.trello_project.domain.attachment.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
}
