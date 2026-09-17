package whitekim.practice.member.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import whitekim.practice.common.exception.NotExistMemberException;
import whitekim.practice.member.dto.request.JoinMember;
import whitekim.practice.member.dto.response.RespMemberInfo;
import whitekim.practice.member.entity.Member;
import whitekim.practice.member.repository.MemberRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher publisher;

    public RespMemberInfo findByMemberInfo(Long memberId) {
        Member member = memberRepository
                .findById(memberId)
                .orElseThrow(NotExistMemberException::new);

        return RespMemberInfo.toDto(member);
    }

    public Long joinMember(JoinMember joinMember) {
        Member saveMember = memberRepository.save(joinMember.toEntity());

        return saveMember.getId();
    }

    public boolean existMemberById(Long memberId) {
        return memberRepository.existsById(memberId);
    }
}
