package jpabook.trello_project.domain.attachment.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import jpabook.trello_project.domain.attachment.dto.response.AttachmentResponseDto;
import jpabook.trello_project.domain.attachment.entity.Attachment;
import jpabook.trello_project.domain.attachment.repository.AttachmentRepository;
import jpabook.trello_project.domain.card.entity.Card;
import jpabook.trello_project.domain.card.repository.CardRepository;
import jpabook.trello_project.domain.common.dto.AuthUser;
import jpabook.trello_project.domain.common.exceptions.InvalidRequestException;
import jpabook.trello_project.domain.workspace_member.entity.WorkspaceMember;
import jpabook.trello_project.domain.workspace_member.enums.WorkRole;
import jpabook.trello_project.domain.workspace_member.repository.WorkspaceMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AttachmentService {
    private final AmazonS3Client amazonS3Client;
    private final AttachmentRepository attachmentRepository;
    private final CardRepository cardRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Transactional
    public List<AttachmentResponseDto> uploadAttachments(List<MultipartFile> files, Long workId, Long cardId, AuthUser authUser) throws IOException {
        Card card = checkCardExist(cardId);
        // 멤버 로직 검사
        WorkspaceMember workspaceMember = checkMemberExist(authUser.getId(), workId);
        // 멤버 권한 검사
        validateWorkRole(workspaceMember.getWorkRole());

        List<AttachmentResponseDto> responseDtos = new ArrayList<>();

        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            String fileUrl = "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/" + fileName;

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            amazonS3Client.putObject(bucket, fileName, file.getInputStream(), metadata);

            Attachment attachment = new Attachment(fileUrl, card);
            Attachment savedAttachment = attachmentRepository.save(attachment);
            responseDtos.add(new AttachmentResponseDto(savedAttachment));
        }
        return responseDtos;
    }

    @Transactional
    public void deleteAttachment(Long attachmentId, Long workId, AuthUser authUser) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new InvalidRequestException("해당 첨부파일을 찾을 수 없습니다."));
        // 멤버 로직 검사
        WorkspaceMember workspaceMember = checkMemberExist(authUser.getId(), workId);
        // 멤버 권한 검사
        validateWorkRole(workspaceMember.getWorkRole());

        String fileName = attachment.getUrl().substring(attachment.getUrl().lastIndexOf("/") + 1);
        amazonS3Client.deleteObject(bucket, fileName);

        attachmentRepository.delete(attachment);
    }

    private Card checkCardExist(Long cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new InvalidRequestException("해당 카드를 찾을 수 없습니다."));
    }

    private WorkspaceMember checkMemberExist(Long id, Long workId) {
        log.info("::: 회원 -> 멤버 검사 로직 동작 :::");
        return workspaceMemberRepository.findByUserIdAndWorkspaceId(id, workId)
                .orElseThrow(() -> new InvalidRequestException("해당 멤버가 없습니다!"));
    }

    private void validateWorkRole(WorkRole workRole) {
        if (workRole == WorkRole.ROLE_READONLY) {
            throw new InvalidRequestException("읽기 전용 권한으로 해당 작업을 수행할 수 없습니다.");
        }
    }
}
