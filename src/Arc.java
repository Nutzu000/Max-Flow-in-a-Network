import java.awt.*;

public class Arc {
    public Point start;
    public Point end;
    public Integer firstNodeNr;
    public Integer secondNodeNr;
    public Integer flux = 0;
    public Integer valoare = 0;

    public Arc() {
        this.start = new Point();
        this.end = new Point();
    }

    public Arc(Point start, Point end) {
        this.start = start;
        this.end = end;
    }

    public Arc(Arc arc) {
        this.start = new Point();
        this.start.x = arc.start.x;
        this.start.y = arc.start.y;
        this.end = new Point();
        this.end.x = arc.end.x;
        this.end.y = arc.end.y;
        this.firstNodeNr = arc.firstNodeNr;
        this.secondNodeNr = arc.secondNodeNr;
        this.valoare = arc.valoare;
    }

    public void drawArc(Graphics g) {
        if (this.start != null) {
            if(flux==0) {
                g.setColor(Color.RED);
            }else if(flux!=0){
                g.setColor(Color.BLUE);
            }
            g.drawLine(this.start.x, this.start.y, this.end.x, this.end.y);
            g.drawString(Integer.toString(this.flux) + '/' + Integer.toString(this.valoare), (this.start.x + this.end.x) / 2, (this.start.y + this.end.y) / 2);
            double angle = Math.atan2(end.y - start.y, end.x - start.x);
            int arrowHeight = 15;
            int halfArrowWidth = 7;
            Point aroBase = new Point(0, 0);
            aroBase.x = (int) (end.x - arrowHeight * Math.cos(angle));
            aroBase.y = (int) (end.y - arrowHeight * Math.sin(angle));
            Point varf1 = new Point(0, 0), varf2 = new Point(0, 0);
            varf1.x = (int) (aroBase.x - halfArrowWidth * Math.cos(angle - Math.PI / 2));
            varf1.y = (int) (aroBase.y - halfArrowWidth * Math.sin(angle - Math.PI / 2));
            varf2.x = (int) (aroBase.x + halfArrowWidth * Math.cos(angle - Math.PI / 2));
            varf2.y = (int) (aroBase.y + halfArrowWidth * Math.sin(angle - Math.PI / 2));
            int deltaX = start.x - end.x;
            int deltaY = start.y - end.y;
            int x[] = {end.x + (int) (deltaX * 0.05), varf1.x + (int) (deltaX * 0.05),
                    varf2.x + (int) (deltaX * 0.05)};
            int y[] = {end.y + (int) (deltaY * 0.05), varf1.y + (int) (deltaY * 0.05),
                    varf2.y + (int) (deltaY * 0.05)};
            int npoints = x.length;// or y.length
            g.drawPolygon(x, y, npoints);// draws polygon outline
            g.fillPolygon(x, y, npoints);// paints a polygon
        }

    }
}