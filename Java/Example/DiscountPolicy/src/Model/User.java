package Model;

public class User {
    private MemberShip memberShip;

    public User() {
        this.memberShip = MemberShip.BRONZE;
    }

    public MemberShip getMemberShip() {
        return memberShip;
    }

    public void setMemberShip(MemberShip memberShip) {
        this.memberShip = memberShip;
    }
}
