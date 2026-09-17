package whitekim.practice.member.event.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import whitekim.practice.member.event.CheckExistMemberEvent;
import whitekim.practice.member.service.MemberService;

@Component
@RequiredArgsConstructor
@Slf4j
public class MemberEventListener {
    private final MemberService memberService;

    @EventListener
    public void checkExistMember(CheckExistMemberEvent memberEvent) {
        boolean existYn = memberService.existMemberById(memberEvent.getMemberId());
        memberEvent.setExist(existYn);

        log.info("[Member] 사용자 사용가능여부 확인 | 사용자ID : {}, 사용가능여부 : {}", memberEvent.getMemberId(), existYn);
    }
}
