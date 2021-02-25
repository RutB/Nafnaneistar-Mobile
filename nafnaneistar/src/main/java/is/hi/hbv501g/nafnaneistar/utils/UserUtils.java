package is.hi.hbv501g.nafnaneistar.utils;

import java.util.ArrayList;
import java.util.Set;

import is.hi.hbv501g.nafnaneistar.nafnaneistar.Entities.NameCard;
import is.hi.hbv501g.nafnaneistar.nafnaneistar.Entities.User;
import is.hi.hbv501g.nafnaneistar.nafnaneistar.Services.NameService;

public class UserUtils {

    /**
     * Initiates the users availableName with all of the names residing in the
     * given nameService
     * @param user - The user to initiate
     * @param nameService - The nameservice with the proper names
     */
    public static void initAvailableNames(User user, NameService nameService){
        ArrayList<Integer> ids = new ArrayList<>();
		for(NameCard nc : nameService.findAll())
            ids.add(nc.getId());
        user.setAvailableNames(ids);
    }
    public static ArrayList<Integer> getAvailableNames(NameService nameService){
        ArrayList<Integer> ids = new ArrayList<>();
		for(NameCard nc : nameService.findAll())
            ids.add(nc.getId());
       return ids;
    }

    /**
     * Returns the Id's of the available id matching the name's gender, used if you only want to
     * access the female or male names available from a user
     * @param user - User to get the names for
     * @param nameService - nameService that has all the names
     * @param gender - female or male names
     * @return - returns a list containing the ids of the available names
     */
    public static ArrayList<Integer> getGenderList(User user, NameService nameService,int gender){
        ArrayList<Integer> genderList = new ArrayList<Integer>();
        ArrayList<NameCard> ncs = (ArrayList<NameCard>) nameService.findAll();
        ncs.removeIf(nc -> nc.getGender() != gender);
        ncs.forEach(n -> { if(user.getAvailableNames().contains(n.getId())) genderList.add(n.getId());});
        return genderList;
    }

    /**
     * Returns the Id's of the selected gender from a given set of integers
     * @param ids - Set of Integers to filter the gender from
     * @param nameService - nameService containing the names
     * @param gender - selected gender
     * @return - returns a list given the criteria
     */
    public static ArrayList<Integer> getGenderList(Set<Integer> ids, NameService nameService,int gender){
        ArrayList<Integer> genderList = new ArrayList<Integer>();
        for(NameCard nc : nameService.findAll()){
            if(nc.getGender() == gender){
                if(ids.contains(nc.getId()))
                    genderList.add(nc.getId());
            }
        }
        return genderList;
    }

    /**
     * Validates if the User is logged in or not
     * @param user - the currentUser
     * @return true or false depending on status
     */
    public static boolean isLoggedIn(User user){
        if(user == null)
            return false;
        return true;
    }

}
