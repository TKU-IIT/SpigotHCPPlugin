/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spigotmc;

import java.sql.*;
import static java.lang.Math.log;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;


import java.util.logging.Logger;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author cangyu
 */
public class HCP extends JavaPlugin{
       private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;
    private static Permission perms = null;
    private static Chat chat = null;

     private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
 
    
    //DataBase vars.
final String username="cangyu"; //Enter in your db username
final String password="jefflin123"; //Enter your password for the db
final String url = "jdbc:mysql://120.126.84.78:3306/rfid_rpi"; //Enter URL w/db name

//Connection vars
static Connection connection; //This is the variable we will use to connect to database

    @Override
    public void onEnable() {
        this.getCommand("hcp").setExecutor(new HCP_host());
                       if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupPermissions();
        setupChat();



    }

 
   
    @Override
    public void onDisable() {
         log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
            // invoke on disable.
    try { //using a try catch to catch connection errors (like wrong sql password...)
        if (connection!=null && !connection.isClosed()){ //checking if connection isn't null to
            //avoid receiving a nullpointer
            connection.close(); //closing the connection field variable.
        }
    } catch(Exception e) {
        e.printStackTrace();
    }
    
    }

    
    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }
    
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
 public class HCP_host implements CommandExecutor {

    // This method is called, when somebody uses our command
       @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        
                try { //We use a try catch to avoid errors, hopefully we don't get any.
                    Class.forName("com.mysql.jdbc.Driver"); //this accesses Driver in jdbc.
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    System.err.println("jdbc driver unavailable!");
                    return true;
                }
                try { //Another try catch to get any SQL errors (for example connections errors)
                    connection = (Connection) DriverManager.getConnection(url, username, password);
                    //with the method getConnection() from DriverManager, we're trying to set
                    //the connection's url, username, password to the variables we made earlier and
                    //trying to get a connection at the same time. JDBC allows us to do this.
                } catch (SQLException e) { //catching errors)
                    e.printStackTrace(); //prints out SQLException errors to the console (if any)
                }
            
                
                if (!(sender instanceof Player)) {
            return false;
        }else{
                    if(args.length==0){
        sender.sendMessage(ChatColor.AQUA+"[HCP] "+ChatColor.GOLD+"Version 0.0.1"+"\n"+ChatColor.GRAY+"http://www.hunchoipass.com");
                    }
        else if ("check".equals(args[0])){

                
                Player player = (Player) sender;
                if(args.length<=1){
                sender.sendMessage(ChatColor.AQUA+"[HCP] "+"\u00A7cToo less arguments!");
                }else{try {
                    String sql = "SELECT * FROM card_info WHERE cardnumber='"+args[1]+"'";
                    
                    PreparedStatement stmt = connection.prepareStatement(sql);
                    ResultSet results = stmt.executeQuery();
                    if (!results.next()) {sender.sendMessage(ChatColor.AQUA+"[HCP] "+"\u00A7cCouldn't Find your Cardnumber!");
                    System.out.println("Failed");
                    } else {sender.sendMessage(ChatColor.AQUA+"[HCP] "+"\u00A72Your Card is registered!");
                    System.out.println("Success");
                    }                   } catch (SQLException ex) {
                        Logger.getLogger(HCP.class.getName()).log(Level.SEVERE, null, ex);
                    }
                
           
                
            }  
        }else if(args[0].equals("register")){
                        try {
                            String sql = "UPDATE `card_info` SET `mc_username`=" + sender.getName()+", `mc_password`="+args[2]+"  WHERE `cardnumber`="+args[1]+";";
                            
                            PreparedStatement stmt = connection.prepareStatement(sql);
                            stmt.executeUpdate();   
                        sender.sendMessage(ChatColor.AQUA+"[HCP] "+ChatColor.GREEN+"Registered Succesfully!");
                        } catch (SQLException ex) {
                            Logger.getLogger(HCP.class.getName()).log(Level.SEVERE, null, ex);
                            sender.sendMessage(ChatColor.AQUA+"[HCP] "+ChatColor.RED+"failed!");
                        }
        }
                    
                    
                    
                    
                    
                    
                }
        // If the player (or console) uses our command correct, we can return true
        return true;
    }
    

}
        public static Economy getEcononomy() {
        return econ;
    }

    public static Permission getPermissions() {
        return perms;
    }

    public static Chat getChat() {
        return chat;
    }

}
