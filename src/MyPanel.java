import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import javax.swing.*;

public class MyPanel extends JPanel {
    private int nodeNr = 1;
    private int node_diam = 30;
    private Vector<Node> listaNoduri = new Vector<>();
    private Vector<Arc> listaArce = new Vector<>();
    public Vector<Vector<Integer>> matriceDeAdiacenta = new Vector<>();
    public Vector<Vector<Integer>> fluxuriInitiale = new Vector<>();
    Point pointStart = null;
    Point pointEnd = null;
    boolean isDragging = false;
    JButton buton;
    JTextField valoareaArcelor;
    JTextField sursaDestinatieField;
    JButton butonArce;
    JButton sursaDestinatie;
    int sursa = -1;
    int destinatie = -1;
    int fluxMaxim = -1;

    void functieButonArce() {
        String aux = valoareaArcelor.getText();
        int numarDeSpatii = 0;
        for (int i = 0; i < aux.length(); i++) {
            if (aux.charAt(i) == ' ') {
                numarDeSpatii++;
            }
        }
        if (numarDeSpatii == 2) {
            int firstNode = Integer.parseInt(aux.substring(0, aux.indexOf(" "))) - 1;
            int secondNode = Integer.parseInt(aux.substring(aux.indexOf(" ") + 1, aux.indexOf(" ", aux.indexOf(" ") + 1))) - 1;
            int arcValue = Integer.parseInt(aux.substring(aux.lastIndexOf(" ") + 1));
            for (int i = 0; i < listaArce.size(); i++) {
                if (listaArce.elementAt(i).firstNodeNr == firstNode &&
                        listaArce.elementAt(i).secondNodeNr == secondNode) {
                    listaArce.elementAt(i).valoare = arcValue;
                    matriceDeAdiacenta.elementAt(firstNode).setElementAt(arcValue, secondNode);
                    repaint();
                    break;
                }
            }
        } else if (numarDeSpatii == 3) {
            int poz = 0, occurences = 0;
            for (int i = 0; i < aux.length(); i++) {
                if (aux.charAt(i) == ' ' && occurences < 2) {
                    occurences++;
                    poz = i;
                } else if (occurences == 2 && aux.charAt(i) == ' ') {
                    occurences = i;
                }
            }
            int firstNode = Integer.parseInt(aux.substring(0, aux.indexOf(" "))) - 1;
            int secondNode = Integer.parseInt(aux.substring(aux.indexOf(" ") + 1, aux.indexOf(" ", aux.indexOf(" ") + 1))) - 1;
            int flux = Integer.parseInt(aux.substring(poz + 1, occurences));
            int arcValue = Integer.parseInt(aux.substring(aux.lastIndexOf(" ") + 1));
            for (int i = 0; i < listaArce.size(); i++) {
                if (listaArce.elementAt(i).firstNodeNr == firstNode &&
                        listaArce.elementAt(i).secondNodeNr == secondNode) {
                    listaArce.elementAt(i).valoare = arcValue;
                    listaArce.elementAt(i).flux = flux;
                    matriceDeAdiacenta.elementAt(firstNode).setElementAt(arcValue, secondNode);
                    repaint();
                    break;
                }
            }
            fluxuriInitiale.elementAt(firstNode).setElementAt(flux, secondNode);
        }

    }

    void functieFordFulkerson() {
        for (int i = 0; i < listaArce.size(); i++) {
            listaArce.elementAt(i).flux = fluxuriInitiale.elementAt(listaArce.elementAt(i).firstNodeNr).elementAt(listaArce.elementAt(i).firstNodeNr);
        }
        fluxMaxim = FordFulkerson();
        repaint();
    }

    void functieSursaSiDestinatie() {
        for (int i = 0; i < fluxuriInitiale.size(); i++) {
            for (int j = 0; j < fluxuriInitiale.elementAt(i).size(); j++) {
                System.out.print(fluxuriInitiale.elementAt(i).elementAt(j));
                System.out.print(" ");
            }
            System.out.println();
        }
        System.out.println();
        String aux = sursaDestinatieField.getText();
        sursa = Integer.parseInt(aux.substring(0, aux.indexOf(" "))) - 1;
        destinatie = Integer.parseInt(aux.substring(aux.lastIndexOf(" ") + 1)) - 1;
        repaint();
    }

    boolean bfs(int grafRezidual[][], int parinti[]) {
        boolean esteVizitat[] = new boolean[matriceDeAdiacenta.size()];
        for (int i = 0; i < matriceDeAdiacenta.size(); ++i)
            esteVizitat[i] = false;

        LinkedList<Integer> queue = new LinkedList<Integer>();
        queue.add(sursa);
        esteVizitat[sursa] = true;
        parinti[sursa] = -1;

        while (queue.size() != 0) {
            int u = queue.poll();

            for (int v = 0; v < matriceDeAdiacenta.size(); v++) {
                if (!esteVizitat[v] && grafRezidual[u][v] > 0) {
                    if (v == destinatie) {
                        parinti[v] = u;
                        return true;
                    }
                    queue.add(v);
                    parinti[v] = u;
                    esteVizitat[v] = true;
                }
            }
        }
        return false;
    }

    int FordFulkerson() {
        int u, v, fluxMaxim = 0;

        int grafRezidual[][] = new int[matriceDeAdiacenta.size()][matriceDeAdiacenta.size()];

        for (u = 0; u < matriceDeAdiacenta.size(); u++)
            for (v = 0; v < matriceDeAdiacenta.size(); v++)
                grafRezidual[u][v] = matriceDeAdiacenta.elementAt(u).elementAt(v)
                        - fluxuriInitiale.elementAt(u).elementAt(v);

        int parinti[] = new int[matriceDeAdiacenta.size()];

        while (bfs(grafRezidual, parinti)) {
            int fluxCale = Integer.MAX_VALUE;
            for (v = destinatie; v != sursa; v = parinti[v]) {
                u = parinti[v];
                fluxCale
                        = Math.min(fluxCale, grafRezidual[u][v]);
            }

            for (v = destinatie; v != sursa; v = parinti[v]) {
                u = parinti[v];
                grafRezidual[u][v] -= fluxCale;
                grafRezidual[v][u] += fluxCale;
                for (int i = 0; i < listaArce.size(); i++) {
                    if (listaArce.elementAt(i).firstNodeNr == u
                            && listaArce.elementAt(i).secondNodeNr == v) {
                        listaArce.elementAt(i).flux += fluxCale;
                    }
                    if (listaArce.elementAt(i).firstNodeNr == v
                            && listaArce.elementAt(i).secondNodeNr == u) {
                        listaArce.elementAt(i).flux -= fluxCale;
                    }
                }
            }

            fluxMaxim += fluxCale;
        }

        return fluxMaxim;
    }

    public MyPanel() {
        this.setBorder(BorderFactory.createLineBorder(Color.black));

        buton = new JButton("Ford Fulkerson");
        buton.setBounds(75, 100, 100, 50);
        buton.addActionListener(e -> functieFordFulkerson());
        add(buton);

        valoareaArcelor = new JTextField();
        valoareaArcelor.setPreferredSize(new Dimension(250, 40));
        add(valoareaArcelor);


        butonArce = new JButton("Adauga valoarea");
        butonArce.setBounds(75, 100, 100, 50);
        butonArce.addActionListener(e -> functieButonArce());
        add(butonArce);

        sursaDestinatieField = new JTextField();
        sursaDestinatieField.setPreferredSize(new Dimension(100, 40));
        add(sursaDestinatieField);

        sursaDestinatie = new JButton("Seteaza sursa si stocul");
        sursaDestinatie.setBounds(75, 100, 100, 50);
        sursaDestinatie.addActionListener(e -> functieSursaSiDestinatie());
        add(sursaDestinatie);


        addMouseListener(new MouseAdapter() {
            // evenimentul care se produce la apasarea mouse-ului
            public void mousePressed(MouseEvent e) {
                pointStart = e.getPoint();
            }

            // evenimentul care se produce la eliberarea mouse-ului
            public void mouseReleased(MouseEvent e) {
                if (!isDragging) {// adaugare nod, fara sa se suprapuna
                    boolean isClose = false;
                    Point newNodeCenter = e.getPoint();
                    newNodeCenter.x = newNodeCenter.x - node_diam / 2;
                    newNodeCenter.y = newNodeCenter.y - node_diam / 2;
                    if (listaNoduri.size() > 0) {
                        for (Node it : listaNoduri) {
                            if (it.getDistance(newNodeCenter) <= node_diam) {
                                isClose = true;
                            }
                        }
                        if (!isClose) {
                            for (Vector<Integer> integers : matriceDeAdiacenta) {
                                integers.add(0);
                            }

                            for (Vector<Integer> integers : fluxuriInitiale) {
                                integers.add(0);
                            }

                            Vector<Integer> row = new Vector<>();
                            for (int j = 0; j < matriceDeAdiacenta.get(0).size(); j++) {
                                row.add(0);
                            }
                            matriceDeAdiacenta.add(row);

                            Vector<Integer> row2 = new Vector<>();
                            for (int j = 0; j < matriceDeAdiacenta.get(0).size(); j++) {
                                row2.add(0);
                            }
                            fluxuriInitiale.add(row2);

                            addNode(e.getX() - node_diam / 2, e.getY() - node_diam / 2);
                        }
                    } else {
                        Vector<Integer> row = new Vector<>();
                        row.add(0);
                        matriceDeAdiacenta.add(row);
                        Vector<Integer> row2 = new Vector<>();
                        row2.add(0);
                        fluxuriInitiale.add(row2);
                        addNode(e.getX() - node_diam / 2, e.getY() - node_diam / 2);
                    }
                } // adaugare arce care pleaca din noduri sau ajung in noduri
                else {
                    boolean pointStartIsOnNode = false;
                    int pointStartNodeNumber = 0;

                    boolean pointEndIsOnNode = false;
                    int pointEndNodeNumber = 0;

                    // pointStart
                    for (int it = 0; it < listaNoduri.size() && !pointStartIsOnNode; it++) {
                        if (listaNoduri.get(it).getDistance(pointStart) < node_diam) {
                            pointStartIsOnNode = true;
                            pointStartNodeNumber = listaNoduri.get(it).getNumber();

                        }
                    }

                    // pointEnd
                    for (int it = 0; it < listaNoduri.size() && !pointEndIsOnNode; it++) {
                        if (listaNoduri.get(it).getDistance(pointEnd) < node_diam) {
                            pointEndIsOnNode = true;
                            pointEndNodeNumber = listaNoduri.get(it).getNumber();
                        }
                    }

                    if (pointStartIsOnNode && pointEndIsOnNode && pointStartNodeNumber != pointEndNodeNumber) {
                        // aici updatam graful de adiacenta
                        matriceDeAdiacenta.get(pointStartNodeNumber - 1).set(pointEndNodeNumber - 1, 1);
                        Arc aux = new Arc(new Point(listaNoduri.elementAt(pointStartNodeNumber - 1).getCoordX() + node_diam / 2, listaNoduri.elementAt(pointStartNodeNumber - 1).getCoordY() + node_diam / 2), new Point(listaNoduri.elementAt(pointEndNodeNumber - 1).getCoordX() + node_diam / 2, listaNoduri.elementAt(pointEndNodeNumber - 1).getCoordY() + node_diam / 2));
                        aux.firstNodeNr = pointStartNodeNumber - 1;
                        aux.secondNodeNr = pointEndNodeNumber - 1;
                        listaArce.add(aux);
                    }
                }

                pointStart = null;
                isDragging = false;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            // evenimentul care se produce la drag&drop pe mousse
            public void mouseDragged(MouseEvent e) {
                pointEnd = e.getPoint();
                isDragging = true;
                repaint();
            }
        });
    }

    private void addNode(int x, int y) {
        Node node = new Node(x, y, this.nodeNr);
        this.listaNoduri.add(node);
        ++this.nodeNr;
        this.repaint();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (fluxMaxim != -1) {
            g.drawString("Fluxul maxim este: " + fluxMaxim, 500, 100);
        }

        // deseneaza arcul curent; cel care e in curs de desenare
        if (pointStart != null) {
            g.setColor(Color.RED);
            g.drawLine(pointStart.x, pointStart.y, pointEnd.x, pointEnd.y);
            double angle = Math.atan2(pointEnd.y - pointStart.y, pointEnd.x - pointStart.x);
            int arrowHeight = 15;
            int halfArrowWidth = 7;
            Point aroBase = new Point(0, 0);
            aroBase.x = (int) (pointEnd.x - arrowHeight * Math.cos(angle));
            aroBase.y = (int) (pointEnd.y - arrowHeight * Math.sin(angle));
            Point varf1 = new Point(0, 0), varf2 = new Point(0, 0);
            varf1.x = (int) (aroBase.x - halfArrowWidth * Math.cos(angle - Math.PI / 2));
            varf1.y = (int) (aroBase.y - halfArrowWidth * Math.sin(angle - Math.PI / 2));
            varf2.x = (int) (aroBase.x + halfArrowWidth * Math.cos(angle - Math.PI / 2));
            varf2.y = (int) (aroBase.y + halfArrowWidth * Math.sin(angle - Math.PI / 2));
            int x[] = {pointEnd.x, varf1.x, varf2.x};
            int y[] = {pointEnd.y, varf1.y, varf2.y};
            int npoints = x.length;// or y.length
            g.drawPolygon(x, y, npoints);// draws polygon outline
            g.fillPolygon(x, y, npoints);// paints a polygon

        }

        for (Arc a : this.listaArce) {
            a.drawArc(g);
        }

        if (this.pointStart != null) {
            g.setColor(Color.RED);
            g.drawLine(this.pointStart.x, this.pointStart.y, this.pointEnd.x, this.pointEnd.y);
        }

        for (int i = 0; i < this.listaNoduri.size(); ++i) {
            this.listaNoduri.elementAt(i).drawNode(g, this.node_diam, sursa + 1, destinatie + 1);
        }
    }
}
