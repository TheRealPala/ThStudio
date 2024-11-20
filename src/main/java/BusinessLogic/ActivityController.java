package BusinessLogic;

import dao.DAO;
import domainModel.Activity;

public class ActivityController <T extends Activity>{
    private DAO<T, Integer> dao;

    //TODO implement after ActivityDao is implemented
//    public ActivityController(DAO<T, Integer> dao){
//        this.dao = dao;
//    }
//    /**
//     * Add a new Activity in the DB
//     *
//     * @param newActivity The new activity
//     *
//     *
//     */
//
//    public String addActivity(T newActivity) throws Exception {
//        dao.insert(newActivity);
//        return "Activity added successfully";
//    }
//
//    /**
//     * Update the activity with the corresponding ID
//     */
//    public String updateActivity(T activity) throws Exception {
//        dao.update(activity);
//        return "Activity updated successfully";
//    }
//
//    /**
//     * Remove from the DB the activity with the corresponding ID
//     */
//    public boolean deleteActivity(int id) throws Exception {
//        return this.dao.delete(id);
//    }

}

