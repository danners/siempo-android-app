package co.siempo.phone.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import co.siempo.phone.app.App;

/**
 * Created by Shahab on 2/16/2017.
 */

public class MainListItem implements Serializable {

    private int id;
    private String title;
    private int drawable;
    private boolean isEnabled = true;
    private MainListItemType itemType = MainListItemType.ACTION;
    private String icon;
    private boolean isVisible;
    private String category="";

    public App app = new App();

    /**
     * Contact Information
     */
    private long contactId;
    private String contactName;
    private List<ContactNumber> numbers;
    private String imageUri;


    public MainListItem(int id, String title, String packageName) {
        this.id = id;
        this.title = title;
        this.app.packageName = packageName;
    }

    public MainListItem(int id, App app) {
        this.id = id;
        this.title = app.displayName;
        this.app = app;
    }

    public MainListItem(int id, String title, String packageName, int drawable) {
        this.id = id;
        this.title = title;
        this.app.packageName = packageName;
        this.drawable = drawable;
    }

    public MainListItem(int id, String title, String icon, MainListItemType itemType) {
        this.id = id;
        this.title = title;
        this.icon = icon;
        this.itemType = itemType;
    }


    public MainListItem(long contactId, int id, String title, String icon, MainListItemType itemType) {
        this.id = id;
        this.contactId = contactId;
        this.contactName = title;
        this.title = title;
        this.icon = icon;
        this.itemType = itemType;
        this.numbers = new ArrayList<>();
    }

    public MainListItem(int id, String title, int drawable, MainListItemType itemType,String category) {
        this.id = id;
        this.title = title;
        this.itemType = itemType;
        this.drawable = drawable;
        this.category=category;
    }

    public MainListItem(MainListItem mainListItem) {
        this.id = mainListItem.getId();
        this.title = mainListItem.getTitle();
        this.itemType = mainListItem.getItemType();
        this.drawable = mainListItem.getDrawable();
        this.isVisible = mainListItem.isVisible();
    }

    /**
     * This constructor is used for load menu item in apps pane
     *
     * @param id       constant id
     * @param title    name
     * @param drawable image id
     */
    public MainListItem(int id, String title, int drawable,String category) {
        this.id = id;
        this.title = title;
        this.drawable = drawable;
        this.category=category;

    }

    public String getPackageName() {
        if (app == null) {
            return null;
        }
        return app.packageName;
    }



    public String getCategory(){
        return  category;
    }
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public MainListItemType getItemType() {
        return itemType;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }


    public int getDrawable() {
        return drawable;
    }


    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    @Override
    public String toString() {
        return "MainListItem{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", isVisible=" + isEnabled +
                ", itemType=" + itemType +
                ", icon='" + icon + '\'' +
                '}';
    }


    public long getContactId() {
        return contactId;
    }

    public String getContactName() {
        return contactName;
    }

    public List<MainListItem.ContactNumber> getNumbers() {
        return numbers;
    }

    public MainListItem.ContactNumber getNumber() {
        return numbers.get(0);
    }

    public void addNumbers(String label, String number) {
        if (!isNumberExists(number))
            getNumbers().add(new ContactNumber(number, label));

    }

    private boolean isNumberExists(String number) {
        String strNum = number.replaceAll("[\\D]", "");
        if (getNumbers() != null) {
            for (MainListItem.ContactNumber strNumber : getNumbers()) {
                String str2 = strNumber.getNumber().replaceAll("[\\D]", "");
                return str2.equals(strNum);

            }
        }
        return false;
    }


    public boolean hasMultipleNumber() {
        if (numbers == null) return false;
        if (numbers.isEmpty()) return false;
        return numbers.size() != 1;
    }


    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public static class ContactNumber {
        private String label;
        private String number;

        public ContactNumber(String number, String label) {
            this.number = number;
            this.label = label;
        }

        public String getNumber() {
            return number;
        }

        @Override
        public String toString() {
            return "ContactNumber{" +
                    "label='" + label + '\'' +
                    ", number='" + number + '\'' +
                    '}';
        }
    }


}
