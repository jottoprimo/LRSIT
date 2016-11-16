/**
 * Created by Evgenij on 16.11.2016.
 */
public class ComboBoxItem {
    private float value;

    public ComboBoxItem(){
        value = 0.5f;
    }

    public ComboBoxItem(float value){
        this.value=value;
    }

    public String ToString(){
        return value*100+"%";
    }
}
