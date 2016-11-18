package cli;

import jdk.nashorn.internal.codegen.CompilerConstants;
import message.MessageSender;
import path.PathDriver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Command line tool to interface with PathDriver in absence of
 * operating system.
 *
 * Created by Brian on 11/9/2016.
 */
public class CommandLineHarness {


    public static void main(String[] args){

        MessageSender sender = new MessageSender(System.out);

        PathDriver driver = new PathDriver(sender);

        PathDriver.CommandLineHarness cli = driver.new CommandLineHarness();

        cli.runCLI();

    }

}
