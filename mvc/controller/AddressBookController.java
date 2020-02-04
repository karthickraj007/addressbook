
package mvc.controller;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import mvc.models.Person;
import mvc.views.ContactDialog;
import mvc.views.AddressBookMainGUI;
import mvc.views.DetailViewPanel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import mvc.dao.AddressBookDAOImplementation;
import mvc.views.NameListPanel;

/**
 *
 * @author Bharathy Annamalai - KGiSL
 */
public class AddressBookController {
    private final AddressBookMainGUI view;
    private ActionListener actionListener;
    private ActionListener choiceListener;
    private ContactDialog dialog;
    private final AddressBookDAOImplementation daoimplement;
    private NameListPanel nlp;
    private DetailViewPanel detailPanel;
    private DetailViewPanel contactDetailsPanel;
    
    public AddressBookController(){
        daoimplement = new AddressBookDAOImplementation();
        view =new AddressBookMainGUI("View");  
    }
     
    
     
    
     
    public void control(){
        loadPersons();
        actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource()== view.getAdd())
                {
                       
                   view.getMainGUIFrame().disable();
                        openAdd();
                       view.getMainGUIFrame().enable();
                     
                }
                else if(e.getSource()== view.getEdit())
                    openEdit();
                else if(e.getSource() == view.getDelete())
                    openDelete();
                else if(e.getSource() == view.getExit())
                    System.exit(0);
            }
        };
        
        view.getAdd().addActionListener(actionListener);
        view.getDelete().addActionListener(actionListener);
        view.getEdit().addActionListener(actionListener);
        view.getExit().addActionListener(actionListener);
      
    }
    
    
    private void openAdd(){   
       view.getMainGUIFrame().setVisible(false);
         dialog =  new ContactDialog("New Entries");    
         dialog.getPanel().setLocation(500,500);
        choiceListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            if(e.getSource()== dialog.getSubmitButton())
            {
            contactDetailsPanel=dialog.getPanel();
            //callDAO("Add");
            Boolean flag = false;
            String name = contactDetailsPanel.getNameField().getText();
            String mobile = contactDetailsPanel.getMobileField().getText();
            String email = contactDetailsPanel.geteMailField().getText();
            String city = contactDetailsPanel.getCityField().getText();
            flag = validate();
            if(flag)
            {    
             Person person = new Person();
             person.setData(name,mobile,email,city);
             daoimplement.addPerson(person);
             dialog.getFrame().dispose();
             view.getMainGUIFrame().setVisible(true);
             loadPersons();
            }
                   
                }
                else if(e.getSource()== dialog.getcancelButton())
                {
                    dialog.getFrame().dispose();
                    view.getMainGUIFrame().setVisible(true);
                }   
            }
            
        };
         dialog.getSubmitButton().addActionListener(choiceListener);
         dialog.getcancelButton().addActionListener(choiceListener);  
    }  
    /*  The following method can be common work for ADD and EDIT operations
     public void callDAO(String operation)
        {
             contactDetailsPanel=dialog.getPanel();
             String name = contactDetailsPanel.getNameField().getText();
             String mobile = contactDetailsPanel.getMobileField().getText();
             String email = contactDetailsPanel.geteMailField().getText();  
             Boolean flag = validate();
             if(flag)
             {  
             Person person = new Person();
             person.setData(name,mobile,email);
             if(operation.equals("Add"))
             {
              daoimplement.addPerson(person);
              dialog.getFrame().dispose();
              
             }
              else
             {
                 daoimplement.updatePerson(person,operation);
                    dialog.getFrame().dispose();
             }
             }
              loadPersons();
        }
     
   */
    
    
    
     /**
     *validates that mobile field and name are not empty
     * @return
     */
    public Boolean validate(){
        boolean valid = false;
        String name = contactDetailsPanel.getNameField().getText();
        String mobile = contactDetailsPanel.getMobileField().getText();
        if (name==null||name.equals("")||(mobile==null||mobile.equals("")))
            JOptionPane.showMessageDialog(new JFrame(), "Fields Marked as * are Mandatory","Inane error", JOptionPane.ERROR_MESSAGE);
        else 
            valid = true;
        return valid;
               
    }
     
    private void openEdit(){
        dialog =  new ContactDialog("Update Entries");
        final String originalName=detailPanel.getNameField().getText();
        dialog.getPanel().setName(detailPanel.getNameField().getText());
        dialog.getPanel().setMobile(detailPanel.getMobileField().getText());
        dialog.getPanel().seteMail(detailPanel.geteMailField().getText());
        dialog.getPanel().setCity(detailPanel.getCityField().getText());
        view.getMainGUIFrame().setVisible(false);
        choiceListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource()== dialog.getSubmitButton())
                {
                Boolean flag = false;
                contactDetailsPanel = dialog.getPanel();
                String name = contactDetailsPanel.getNameField().getText();
                String mobile = contactDetailsPanel.getMobileField().getText();
                String email = contactDetailsPanel.geteMailField().getText(); 
                String city = contactDetailsPanel.getCityField().getText(); 
                flag = validate();
                if(flag)
                {    
                Person person = new Person();
                person.setData(name,mobile,email,city);
                daoimplement.updatePerson(person,originalName);
                dialog.getFrame().dispose();
                view.getMainGUIFrame().setVisible(true);
                loadPersons();
                }
                }
                else if(e.getSource()== dialog.getcancelButton())
                {
                    dialog.getFrame().dispose();
                     view.getMainGUIFrame().setVisible(true);
                }   
            }
        };
         dialog.getSubmitButton().addActionListener(choiceListener);
         dialog.getcancelButton().addActionListener(choiceListener);
    }
        
    private void openDelete(){
        final String name=detailPanel.getNameField().getText();
        int reply = JOptionPane.showConfirmDialog(
                null,
                " Are You Sure to remove "+name +" permanently?", 
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        
        if (reply == JOptionPane.YES_OPTION) {
          daoimplement.removePerson(name);
          loadPersons();
        }
    }
    
    public void loadPersons()       
    {
        nlp = view.getNameListPanel();
        daoimplement.getAllNames(nlp);
        nlp.getJList().setSelectedIndex(0);
       
        if(nlp.getJList().getSelectedValue() != null)
        {  
        String selectedName = nlp.getJList().getSelectedValue().toString();
        detailPanel=view.getDetailViewPanel();
        daoimplement.getSelectedName(detailPanel,selectedName);
        nlp.getJList().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        nlp.getJList().addListSelectionListener(new ListSelectionListener()
                {
                    @Override
                    public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting()){
                    JList source = (JList)event.getSource();

                     if(source.getSelectedIndex() == -1)
                         source.setSelectedIndex(0);
                    String select = source.getSelectedValue().toString();
                    getSelectedPerson(select);
                    
                    }
                    }
                    });  
         detailPanel.getNameField().setEditable(false);
         detailPanel.getMobileField().setEditable(false);
         detailPanel.geteMailField().setEditable(false);
         detailPanel.getCityField().setEditable(false);
        }
    }  //loadPersons ends
    
    public void getSelectedPerson(String selectedName)
    {
        detailPanel=view.getDetailViewPanel();
        daoimplement.getSelectedName(detailPanel,selectedName);
     
    }
  
}