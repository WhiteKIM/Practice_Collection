package whitekim.practice.member.event;

import lombok.Getter;
import lombok.Setter;

@Getter
public class CheckExistMemberEvent {
    private Long memberId;

    @Setter
    private boolean isExist;

    public CheckExistMemberEvent(Long memberId) {
        this.memberId = memberId;
        isExist = false;
    }
}
