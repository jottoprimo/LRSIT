import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

/**
 * Created by Evgenij on 15.11.2016.
 */
public class ReportDialog extends JDialog {
    private JTextArea report;
    private boolean result;
    public boolean getResult(){
        return result;
    }

    public ReportDialog(Frame parentFrame, String reportText){
        super(parentFrame, true);
        result = false;
        setSize(new Dimension(300,300));
        report = new JTextArea();
        report.setSize(300,300);
        report.setEditable(false);
        report.setBackground(new Color(227, 228, 254));
        report.setText(reportText);
        Box buttons = Box.createHorizontalBox();
        Button okButton = new Button("OK");
        okButton.addActionListener(e -> {
            result = true;
            dispose();
        });
        Button cancelButton = new Button("Cancel");
        cancelButton.addActionListener(e -> {
            result = false;
            dispose();
        });
        buttons.add(okButton);
        buttons.add(cancelButton);
        report.setText(reportText.toString());
        add(report,BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }

}
