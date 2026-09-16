package whitekim.practice.common.exception;

public class NotExistMemberException extends RuntimeException {
    public NotExistMemberException() {
        super("존재하지 않는 사용자 정보입니다.");
    }
}
