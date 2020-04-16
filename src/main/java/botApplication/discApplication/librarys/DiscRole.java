package botApplication.discApplication.librarys;

import java.io.Serializable;
import java.util.ArrayList;

public class DiscRole implements Serializable {

    public static final long serialVersionUID = 42L;

    private String id = "";
    private String name = "";

    private ArrayList<RoleType> roleTypes = new ArrayList<>();

    public enum RoleType{
        TempGamer, Member, Admin, Mod, other, Group1, Group2, Group3, Group4, Group5, Group6
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<RoleType> getRoleTypes() {
        return roleTypes;
    }

    public void setRoleTypes(ArrayList<RoleType> roleTypes) {
        this.roleTypes = roleTypes;
    }

    public void addRoleType(RoleType roleType){
        if(!roleTypes.contains(roleType)){
            roleTypes.add(roleType);
        }
    }

    public static DiscRole.RoleType getRoleTypeFromString(String s){
        DiscRole.RoleType roleType = null;
        if(s.toLowerCase().startsWith("group")){
            switch (s.toLowerCase().substring(5)){
                case "1":
                    roleType = DiscRole.RoleType.Group1;
                    break;

                case "2":
                    roleType = DiscRole.RoleType.Group2;
                    break;

                case "3":
                    roleType = DiscRole.RoleType.Group3;
                    break;

                case "4":
                    roleType = DiscRole.RoleType.Group4;
                    break;

                case "5":
                    roleType = DiscRole.RoleType.Group5;
                    break;

                case "6":
                    roleType = DiscRole.RoleType.Group6;
                    break;
            }
            return roleType;
        }
        switch (s.toLowerCase()){
            case "admin":
            case "adminiestrator":
                roleType = DiscRole.RoleType.Admin;
                break;

            case "mod":
            case "moderator":
                roleType = DiscRole.RoleType.Mod;
                break;

            case "member":
                roleType = DiscRole.RoleType.Member;
                break;

            case "gamer":
            case "tempgamer":
                roleType = DiscRole.RoleType.TempGamer;
                break;

            default:
            case "other":
                roleType = DiscRole.RoleType.other;
                break;
        }
        return roleType;
    }
}
