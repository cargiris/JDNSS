package edu.msudenver.cs.jdnss;

import java.io.IOException;
import java.lang.AssertionError;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ObjectMessage;

class TCP extends Thread
{
    private ServerSocket ssocket;
    private JDNSS dnsService;
    private Logger logger = JDNSS.getLogger();
    private int threadPoolSize = JDNSS.getJdnssArgs().threads;
    private int port = JDNSS.getJdnssArgs().port;
    private int backlog = JDNSS.getJdnssArgs().backlog;
    private String ipaddress = JDNSS.getJdnssArgs().IPaddress;

    public TCP(JDNSS dnsService) throws UnknownHostException, IOException
    {
        this.dnsService = dnsService;
        logger.traceEntry(new ObjectMessage(dnsService));

        try
        {
            if (ipaddress != null)
            {
                ssocket = new ServerSocket (port, backlog,
                    InetAddress.getByName(ipaddress));
            }
            else if (backlog != 0)
            {
                ssocket = new ServerSocket(port, backlog);
            }
            else
            {
                ssocket = new ServerSocket(port);
            }
        }
        catch (UnknownHostException uhe)
        {
            logger.catching(uhe);
            throw uhe;
        }
        catch (IOException ioe)
        {
            logger.catching(ioe);
            throw ioe;
        }

        logger.traceExit();
    }

    public void run()
    {
        logger.traceEntry();

        Socket socket = null;
        ExecutorService pool = Executors.newFixedThreadPool(threadPoolSize);

        while (true)
        {
            try
            {
                socket = ssocket.accept();
            }
            catch (IOException ioe)
            {
                logger.catching(ioe);
                return;
            }

            logger.trace("Received TCP packet");

            Future f = pool.submit(new TCPThread(socket, dnsService));

            // if we're only supposed to answer once, and we're the first,
            // bring everything down with us.
            if (JDNSS.getJdnssArgs().once)
            {
                try
                {
                    f.get();
                }
                catch (InterruptedException ie)
                {
                    logger.catching(ie);
                }
                catch (ExecutionException ee)
                {
                    logger.catching(ee);
                }

                System.exit(0);
            }
        }
    }
}
