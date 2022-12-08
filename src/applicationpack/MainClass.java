package applicationpack;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

class MainPanel extends JPanel
{
    private final JLabel              lblCaption,lblID,lblName,lblAddress,lblPhone,lblSex,lblCourse,lblIDValue;
    private final JTextField          txtName,txtAddress, txtPhone;
    private final JComboBox           cbxSex,cbxCourse;
    private final JButton             btnAddNew,btnSubmit,btnCancel,btnCommit,btnSearchAll,btnSearchID,btnSearchName,btnSearchCourse;
    private final JTable              tabStudentData;
    private final JScrollPane         scpStudentData;
    private final DefaultTableModel   tblModel;
    private final TableColumnModel    tblColModel;
    private final TableColumn         tblColID,tblColName,tblColAddress,tblColPhone,tblColSex,tblColCourse;
    private final Font                font = new Font("Courier New", Font.BOLD, 16);
    private Connection                con;
    private Statement                 smt;
    private int                       studentID;
    private int                       lastRowIndex = 0;
    private boolean                   unsavedData = false;
    
    private JLabel makeLabel(String cap,int x,int y,int w,int h,int mode)
    {
        JLabel temp = new JLabel(cap);
        if(mode == 0)
        {
            temp.setOpaque(true);
            temp.setBackground(Color.BLUE);
            temp.setForeground(Color.WHITE);
            temp.setFont(new Font("Verdana", 1, 35));
            temp.setHorizontalAlignment(JLabel.CENTER);
            Border b1 = BorderFactory.createLineBorder(Color.RED, 3);
            Border b2 = BorderFactory.createLineBorder(Color.WHITE, 2);
            Border b3 = BorderFactory.createCompoundBorder(b1, b2);
            temp.setBorder(b3);
        }
        else if(mode == 1)
            temp.setFont(font);
        else if(mode == 2)
        {
            temp.setOpaque(true);
            temp.setBackground(Color.WHITE);
            temp.setForeground(Color.BLACK);
            temp.setFont(font);
            temp.setHorizontalAlignment(JLabel.CENTER);
            Border b1 = BorderFactory.createLineBorder(Color.BLACK, 1);
            temp.setBorder(b1);
        }
        temp.setBounds(x,y,w,h);
        super.add(temp);
        return temp;
    }
    private JTextField makeTextField(int x,int y,int w,int h,boolean b)
    {
        JTextField temp = new JTextField();
        temp.setBounds(x,y,w,h);
        temp.setFont(font);
        temp.setForeground(Color.BLACK);
        temp.setHorizontalAlignment(JTextField.CENTER);
        Border b1 = BorderFactory.createLineBorder(Color.BLACK, 1);
        temp.setBorder(b1);
        temp.setEnabled(b);
        super.add(temp);
        return temp;
    }
    private JButton makeButton(String cap,int x,int y,int w,int h,boolean b)
    {
        JButton temp = new JButton(cap);
        temp.setBounds(x,y,w,h);
        temp.setFont(font);
        temp.setMargin(new Insets(0,0,0,0));
        ActionListener act = new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                Object ob = e.getSource();
                if(ob == btnAddNew)
                {
                    enableDisable();
                    reset();
                    studentID++;
                    lblIDValue.setText(String.valueOf(studentID));
                }
                else if(ob == btnSubmit)
                {
                    String id       = lblIDValue.getText();
                    String name     = txtName.getText();
                    String address  = txtAddress.getText();
                    String phone    = txtPhone.getText();
                    String sex      = (String)cbxSex.getSelectedItem();
                    String course   = (String)cbxCourse.getSelectedItem();
                    tblModel.addRow(new String[]{id,name,address,phone,sex,course});
                    btnCommit.setEnabled(true);
                    enableDisable();
                    unsavedData = true;
                }
                else if(ob == btnCancel)
                {
                    enableDisable();
                    studentID--;
                }
                else if(ob == btnCommit)
                {
                    saveRecord();
                    unsavedData = false;
                }
                else if(ob == btnSearchAll)
                {
                    if(unsavedData == true)
                    {
                        saveRecord();
                        unsavedData = false;
                    }
                    tblModel.setRowCount(0);
                    String qry = "SELECT ID,NAME,ADDRESS,PHONE,SEX,COURSE FROM STUDENT ORDER BY ID";
                    populateTable(qry);
                }
                else if(ob == btnSearchID)
                {
                    createSearchFrame("ID");
                }
                else if(ob == btnSearchName)
                {
                    createSearchFrame("Name");
                }
                else if(ob == btnSearchCourse)
                {
                    createSearchFrame("Course");
                }
            }
        };
        temp.addActionListener(act);
        temp.setEnabled(b);
        super.add(temp);
        return temp;
    }
    private void createSearchFrame(String msg)
    {
        try
        {
            SearchFrame frame = new SearchFrame(msg);
            frame.setSize(400,230);
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            frame.setVisible(true);
        }
        catch(Exception ex)
        {
            JOptionPane.showMessageDialog(null, ex);
        }
    }
    private void saveRecord()
    {
        try
        {
            while(lastRowIndex<tabStudentData.getRowCount())
            {
                int id      = Integer.parseInt((String)tabStudentData.getValueAt(lastRowIndex,0));
                String name = "'"+(String)tabStudentData.getValueAt(lastRowIndex,1)+"'";
                String addr = "'"+(String)tabStudentData.getValueAt(lastRowIndex,2)+"'";
                String ph   = "'"+(String)tabStudentData.getValueAt(lastRowIndex,3)+"'";
                String sex  = "'"+(String)tabStudentData.getValueAt(lastRowIndex,4)+"'";
                String crs  = "'"+(String)tabStudentData.getValueAt(lastRowIndex,5)+"'";
                String qry  = "INSERT INTO STUDENT VALUES("+id+","+name+","+addr+","+ph+","+sex+","+crs+")";
                smt.executeUpdate(qry);
                lastRowIndex++;
            }
            btnCommit.setEnabled(false);
            JOptionPane.showMessageDialog(null, "Records Committed Successfully");
        }
        catch(Exception ex)
        {
            JOptionPane.showMessageDialog(null, ex);
        }
    }
    private JComboBox makeComboBox(String sub[],int x,int y,int w,int h,boolean b)
    {
        JComboBox temp = new JComboBox(sub);
        temp.setBounds(x,y,w,h);
        temp.setFont(new Font("Courier New", 1, 18));
        temp.setEnabled(b);
        temp.setSelectedIndex(-1);
        add(temp);
        return temp;
    }
    private TableColumn makeTableColumn(int i,String cap,int width,DefaultTableCellRenderer centerRenderer)
    {
        TableColumn temp = new TableColumn(i);
        temp.setHeaderValue(cap);
        temp.setMaxWidth(width);
        temp.setCellRenderer(centerRenderer);
        tblColModel.addColumn(temp);
        return temp;
    }
    private String getColCaption(String cap)
    {
        String caption = "<html><p style='font-family: Verdana;font-weight: bold;font-size:13pt'>"+cap+"</p></html>";
        return caption;
    }
    private void enableDisable()
    {
        txtName.setEnabled(!txtName.isEnabled());
        txtAddress.setEnabled(!txtAddress.isEnabled());
        txtPhone.setEnabled(!txtPhone.isEnabled());
        cbxSex.setEnabled(!cbxSex.isEnabled());
        cbxCourse.setEnabled(!cbxCourse.isEnabled());
        btnAddNew.setEnabled(!btnAddNew.isEnabled());
        btnSubmit.setEnabled(!btnSubmit.isEnabled());
        btnCancel.setEnabled(!btnCancel.isEnabled());
        btnSearchAll.setEnabled(!btnSearchAll.isEnabled());
        btnSearchID.setEnabled(!btnSearchID.isEnabled());
        btnSearchName.setEnabled(!btnSearchName.isEnabled());
        btnSearchCourse.setEnabled(!btnSearchCourse.isEnabled());
    }
    private void reset()
    {
        txtName.setText("");
        txtAddress.setText("");
        txtPhone.setText("");
        cbxSex.setSelectedIndex(-1);
        cbxCourse.setSelectedIndex(-1);
        txtName.grabFocus();
    }
    private void applicationInit()
    {
        try
        {
            Class .forName("oracle.jdbc.driver.OracleDriver");
            con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE","SCOTT","TIGER");
            DatabaseMetaData dbMeta = con.getMetaData();
            ResultSet dbaseRSet = dbMeta.getTables(null,"SCOTT","STUDENT",new String[]{"TABLE"});
            smt = con.createStatement();
            String qry = "";
            if(!dbaseRSet.next())
            {
                qry = "CREATE TABLE STUDENT(ID NUMBER(4) PRIMARY KEY,NAME VARCHAR(20),ADDRESS VARCHAR(20),PHONE VARCHAR(10),SEX VARCHAR(6),COURSE VARCHAR(5))";
                smt.executeUpdate(qry);
                studentID = 1000;
            }
            else
            {
                qry = "SELECT ID,NAME,ADDRESS,PHONE,SEX,COURSE FROM STUDENT ORDER BY ID";
                populateTable(qry);
            }
        }
        catch(Exception ex)
        {
            JOptionPane.showMessageDialog(null, ex);
        }
    }
    private void populateTable(String qry)
    {
        try
        {
            ResultSet qryRSet = smt.executeQuery(qry);
            while(qryRSet.next())
            {
                studentID       = qryRSet.getInt(1);
                String name     = qryRSet.getString(2);
                String address  = qryRSet.getString(3);
                String phone    = qryRSet.getString(4);
                String sex      = qryRSet.getString(5);
                String course   = qryRSet.getString(6);
                tblModel.addRow(new String[]{String.valueOf(studentID),name,address,phone,sex,course});
                lastRowIndex    = qryRSet.getRow();
            }
        }
        catch(Exception ex)
        {
            JOptionPane.showMessageDialog(null, ex);
        }
    }
    public MainPanel()
    {
        Toolkit tk = Toolkit.getDefaultToolkit();
        lblCaption      = makeLabel("STUDENT RECORD MANAGEMENT SYSTEM",10,10,tk.getScreenSize().width-20,60,0);
        lblID           = makeLabel("STUDENT ID",         10, 80,200,30,1);
        lblIDValue      = makeLabel("",                  210, 80,250,30,2);
        lblName         = makeLabel("STUDENT NAME",       10,120,200,30,1);
        txtName         = makeTextField(                 210,120,250,30,false);
        lblAddress      = makeLabel("ADDRESS",            10,160,200,30,1);
        txtAddress      = makeTextField(                 210,160,250,30,false);
        lblPhone        = makeLabel("PHONE NUMBER",       10,200,200,30,1);
        txtPhone        = makeTextField(                 210,200,250,30,false);
        lblSex          = makeLabel("SEX",                10,240, 50,30,1);
        cbxSex          = makeComboBox(new String[]{"Male","Female","Other"},70,240,120,30,false);
        lblCourse       = makeLabel("COURSE",            210,240, 80,30,1);
        cbxCourse       = makeComboBox(new String[]{"BTech","BCA","BBA","BSc","MTech","MCA","MBA"},310,240,150,30,false);
        btnAddNew       = makeButton("Add New",          10,280,100,30,true);
        btnSubmit       = makeButton("Submit",          127,280,100,30,false);
        btnCancel       = makeButton("Cancel",          244,280,100,30,false);
        btnCommit       = makeButton("Commit",          361,280,100,30,false);
        btnSearchAll    = makeButton("Search All",       10,320,217,30,true);
        btnSearchID     = makeButton("Search by ID",    244,320,217,30,true);
        btnSearchName   = makeButton("Search by Name",   10,360,217,30,true);
        btnSearchCourse = makeButton("Search by Course",244,360,217,30,true);
        
        int tableWidth  = tk.getScreenSize().width-480;
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        tblColModel     = new DefaultTableColumnModel();
        tblColID        = makeTableColumn(0,getColCaption("ID No"),       (int)(tableWidth*0.10),centerRenderer); //10%
        tblColName      = makeTableColumn(1,getColCaption("STUDENT NAME"),(int)(tableWidth*0.25),centerRenderer); //25%
        tblColAddress   = makeTableColumn(2,getColCaption("ADDRESS"),     (int)(tableWidth*0.25),centerRenderer); //25%
        tblColPhone     = makeTableColumn(3,getColCaption("PHONE NUMBER"),(int)(tableWidth*0.20),centerRenderer); //20%
        tblColSex       = makeTableColumn(4,getColCaption("SEX"),         (int)(tableWidth*0.10),centerRenderer); //10%
        tblColCourse    = makeTableColumn(5,getColCaption("COURSE"),      (int)(tableWidth*0.10),centerRenderer); //10%
        
        tblModel        = new DefaultTableModel();
        tblModel.setColumnCount(6);
        
        tabStudentData  = new JTable(tblModel,tblColModel);
        tabStudentData.setFont(font);
        tabStudentData.setRowHeight(25);
        tabStudentData.setEnabled(false);
        
        scpStudentData  = new JScrollPane(tabStudentData);
        scpStudentData.setBounds(470,80,tableWidth,310);
        scpStudentData.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        super.add(scpStudentData);
        
        applicationInit();
    }
}
class MainFrame extends JFrame
{
    public MainFrame(String cap)
    {
        super.setTitle(cap);
        MainPanel panel = new MainPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.YELLOW);
        super.add(panel);
    }
}
public class MainClass
{
    public static void main(String[] args) 
    {
        Toolkit tk = Toolkit.getDefaultToolkit();
        MainFrame frame = new MainFrame("Student Record Management System");
        frame.setSize(tk.getScreenSize().width, 440);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }    
}