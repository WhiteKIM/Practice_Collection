package whitekim.practice.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import whitekim.practice.member.dto.request.JoinMember;
import whitekim.practice.member.dto.response.RespMemberInfo;
import whitekim.practice.member.service.MemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/{memberId}")
    public ResponseEntity<RespMemberInfo> getMemberInfo(@PathVariable Long memberId) {
        RespMemberInfo memberInfo = memberService.findByMemberInfo(memberId);

        return ResponseEntity.ok(memberInfo);
    }

    @PostMapping
    public ResponseEntity<String> joinMember(@RequestBody JoinMember joinMember) {
        memberService.joinMember(joinMember);
        
        return ResponseEntity.ok("회원가입 성공");
    }
}
