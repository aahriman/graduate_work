package cz.cuni.mff.vojta.mobile.models;

import com.google.android.gms.plus.model.people.Person;

/**
 * Created by vojta on 11. 12. 2015.
 */
public class LoginModel {

    public static LoginModel SINGLETON = new LoginModel();

    private Person person;
    private String email;

    private LoginModel() {}

    public String getPersonId(){
        return person != null ? person.getId() : null;
    }

    public String getPersonName(){
        return person != null ? person.getDisplayName() : null;
    }

    public String getPersonEmail(){
        return email;
    }

    public void setPerson(Person person){
        this.person = person;
    }

    public void setEmail(String email){
        this.email = email;
    }
}
