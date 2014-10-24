package connect5.ia;

/*
 * Si vous utilisez Java, vous devez modifier ce fichier-ci.
 *
 * Vous pouvez ajouter d'autres classes sous le package connect5.ia.
 *
 * Christian Telemaque    (TELC10058803)
 * Claude-Clément YAPO    (YAPC01129002)
 */
import connect5.Grille;
import connect5.GrilleVerificateur;
import connect5.Joueur;
import connect5.Position;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

public class JoueurArtificiel implements Joueur {

    private final Random random = new Random();
    private static int nbrJouer = 0;
    private int id = 0;
    private ArrayList<Integer> casesvides;
    private int dernierXJouer = 0;
    private int dernierYJouer = 0;

    public JoueurArtificiel() {
        ++nbrJouer;

        id = nbrJouer == 1 ? 1 : nbrJouer == 2 ? 2 : -1;
    }

    /**
     * Voici la fonction à modifier. Évidemment, vous pouvez ajouter d'autres
     * fonctions dans JoueurArtificiel. Vous pouvez aussi ajouter d'autres
     * classes, mais elles doivent être ajoutées dans le package connect5.ia.
     * Vous de pouvez pas modifier les fichiers directement dans connect., car
     * ils seront écrasés.
     *
     * @param grille Grille reçu (état courrant). Il faut ajouter le prochain
     * coup.
     * @param delais Délais de rélexion en temps réel.
     * @return Retourne le meilleur coup calculé.
     */
    @Override
    public Position getProchainCoup(Grille grille, int delais) {
        ArrayList<Integer> casesvides = new ArrayList<Integer>();
        int nbcol = grille.getData()[0].length;
        for (int l = 0; l < grille.getData().length; l++) {
            for (int c = 0; c < nbcol; c++) {
                if (grille.getData()[l][c] == 0) {
                    casesvides.add(l * nbcol + c);
                }
            }
        }

        this.casesvides = casesvides;

        this.dernierXJouer = (grille.getData().length / 2);
        this.dernierYJouer = (nbcol / 2);

        int choix = Minimax(grille, casesvides);

        //int choix = random.nextInt(casesvides.size());
        choix = casesvides.get(choix);
        return new Position(choix / nbcol, choix % nbcol);
    }

    @Override
    public String getAuteurs() {
        return "Christian TÉLÉMAQUE (TELC10058803)  et  Claude-Clément YAPO (YAPC01129002)";
    }

    public int Minimax(Grille grille, ArrayList<Integer> casesvides) {

        double max = Double.NEGATIVE_INFINITY;

        Vector<Grille> succ = create_successeur(grille, casesvides);

        return MinimaxDecision(succ);

    }

    private Vector<Grille> create_successeur(Grille cloneGrille, ArrayList<Integer> casesvides) {

        Vector<Grille> chd = new Vector();

        int nbcol = cloneGrille.getData()[0].length;
        int lignes = cloneGrille.getData().length;
        Grille tmpGrille;

        for (int i : casesvides) {
            tmpGrille = cloneGrille.clone();
            tmpGrille.getData()[i / lignes][i % nbcol] = (byte) (id);
            chd.add(tmpGrille);
        }

        return chd;
    }

    private int MinimaxDecision(Vector<Grille> succ) {

        int max = -999999999;
        int cmp = -1;
        int posSucc = 0;
        Vector action = new Vector();
        int meilleur = 0;

        for (Grille g : succ) {

            cmp = utilite(g, this.casesvides.get(posSucc));
            System.out.println("utilié = " + cmp);
            if (cmp > max) {
                action = new Vector();
                max = cmp;
                action.add(this.casesvides.get(posSucc));
            } else if (cmp == max) {
                action.add(this.casesvides.get(posSucc));
            }
            ++posSucc;
        }
        
        
        Vector ml =meilleurLigne(action);
        Vector mc =meilleurColonne(action);
        Vector md =meilleurDiagonale(action);
        
        meilleur = (int) ((ml.size() < mc.size() && mc.size() < md.size() ) 
                ?  ml.get(0)
                : (mc.size()<ml.size() && ml.size() < md.size())
                ?  mc.get(0)
                : (md.size() < ml.size() && ml.size() < mc.size())
                ?  md.get(0)
                : 0);

        return meilleur;

    }

    private int utilite(Grille grille, int posSucc) {

        int Maxligne = 0;
        int Maxcol = 0;
        int Maxdiag = 0;

        int Minligne = 0;
        int Mincol = 0;
        int Mindiag = 0;

        int nbcol = grille.getData()[0].length;
        int lignes = grille.getData().length;
        int profondeur = nbcol * lignes;

        byte[][] donne = grille.getData();

        //evaluation des lignes
        for (int i = 0; i < lignes; ++i) {
            int evalLigneMax = 0;
            int evalLigneMin = 0;
            for (int j = 0; j < nbcol; ++j) {

                evalLigneMax = (donne[i][j] == 0 || donne[i][j] == id) ? ++evalLigneMax : 0;
                evalLigneMin = (donne[i][j] == 0||donne[i][j] != id) ? ++evalLigneMin  : 0;
                Maxligne = evalLigneMax == 5 ? ++Maxligne : Maxligne;
                Minligne = evalLigneMin == 5 ? ++Minligne :Minligne;

                j = ((evalLigneMax >= 5)&&(evalLigneMin>=5)) ? nbcol : j;

            }
        }

        //evaluation des colonne
        for (int j = 0; j < nbcol; ++j) {
            int evalcolonneMax = 0;
            int evalcolonneMin = 0;
            for (int i = 0; i < lignes; ++i) {

                evalcolonneMax = (donne[i][j] == 0 || donne[i][j] == id) ? ++evalcolonneMax : 0;
                evalcolonneMin = (donne[i][j] == 0||donne[i][j] != id) ? ++evalcolonneMin : 0;
                Maxcol = (evalcolonneMax == 5) ? ++Maxcol : Maxcol;
                Mincol = (evalcolonneMin == 5) ? ++Mincol: Mincol;

                i = ((evalcolonneMax >= 5)&&(evalcolonneMin>=5)) ? lignes : i;

            }
        }

        //evaluation diagonale
        Maxdiag += diagonaleMax(donne, posSucc, lignes, nbcol);

        //evaluation min
         Mindiag+= diagonaleMin(donne,posSucc, lignes, nbcol);
         
        return ((Maxligne + Maxcol + Maxdiag)-(Minligne+Mincol+Mindiag));
    }

    public int diagonaleMax(byte[][] donne, int posSucc, int lignes, int nbcol) {

        int x = posSucc / lignes;
        int y = posSucc % nbcol;
        int DebutdiagonaleX = x < y ? x - x : x - y;
        int DebutdiagonaleY = y < x ? y - y : y - x;
        int maxDiag = 0;
        int longueurDiagonale = lignes < nbcol ? lignes : nbcol < lignes ? nbcol : nbcol;
        int nbIdDiag = 0;

        int diagonaleBasX = x < y ? x + x : x + y;
        int diagonaleBASY = y < x ? y - y : y - x;

        diagonaleBASY = diagonaleBasX >= lignes ? (diagonaleBASY + (diagonaleBasX - (lignes - 1))) : diagonaleBASY;
        diagonaleBasX = diagonaleBasX >= lignes ? (diagonaleBasX - (diagonaleBasX - (lignes - 1))) : diagonaleBasX;

        // premiere diagonale 
        for (int i = 0; i < longueurDiagonale; ++i) {

            nbIdDiag = (donne[DebutdiagonaleX + i][DebutdiagonaleY + i] == id
                    || donne[DebutdiagonaleX + i][DebutdiagonaleY + i] == 0) ? ++nbIdDiag : 0;
            maxDiag = nbIdDiag == 5 ? ++maxDiag : maxDiag;
            i = nbIdDiag == 5 ? longueurDiagonale : i;
            i = (((DebutdiagonaleX + i) > (DebutdiagonaleY + i))
                    ? ((DebutdiagonaleX + i) == (lignes - 1) ? longueurDiagonale : i)
                    : ((DebutdiagonaleY + i) == (nbcol - 1) ? longueurDiagonale : i));
        }

        // deuxieme diagonale
        nbIdDiag = 0;
        for (int i = 0; i < longueurDiagonale; ++i) {

            nbIdDiag = (donne[diagonaleBasX - i][diagonaleBASY + i] == id
                    || donne[diagonaleBasX - i][diagonaleBASY + i] == 0) ? ++nbIdDiag : 0;
            maxDiag = nbIdDiag == 5 ? ++maxDiag : maxDiag;
            i = nbIdDiag == 5 ? longueurDiagonale : i;
            i = ((diagonaleBASY + i) >= (nbcol - 1)) || ((diagonaleBasX - i) <= 0) ? longueurDiagonale : i;

        }

        return maxDiag;
    }
    
    
    public int diagonaleMin(byte[][] donne, int posSucc, int lignes, int nbcol) {
        
        
        
        return 0;
    }

    private Vector meilleurLigne(Vector action) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private Vector meilleurColonne(Vector action) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private Vector meilleurDiagonale(Vector action) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
