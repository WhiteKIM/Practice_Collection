package whitekim.practice.member.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String memberName;
    private String memberPassword;
    private String memberEmail;

    public Member(String memberName, String memberPassword, String memberEmail) {
        this.memberName = memberName;
        this.memberPassword = memberPassword;
        this.memberEmail = memberEmail;
    }
}
