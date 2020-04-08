import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import org.json.*;


public class fonction {		
	private String id = "5e7554d14ac800a91d7f18caf541a43bd8d5a01eac61f157fc0ca60ef1c0ac0c";
	
	public fonction(String id) {
		this.setId(id);
	}
	
	public HashMap<String, Integer> afficherContacts() {
		URL url;
		HashMap<String, Integer> contact = new HashMap<String,Integer>();
		try {
			url = new URL("https://trankillprojets.fr/wal/wal.php?relations&identifiant="+getId());
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestProperty("Content-Type", "application/json");
			String contentType = con.getHeaderField("Content-Type");

			BufferedReader in = new BufferedReader(
			  new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer content = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
			    content.append(inputLine);
			}
			in.close();
			
			JSONObject json = new JSONObject(content.toString());
			JSONArray relations = json.getJSONArray("relations");
			
			for(int i = 0; i < relations.length(); i++) {
				contact.put(relations.getJSONObject(i).getString("identite"), relations.getJSONObject(i).getInt("relation"));
			}	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return contact;
	}
	
	/**
	 * Envoie un message à l'utilisateur associé à l'id_relation
	 * @param id_relation Correspond à l'id relation dans la liste de contact
	 * @param message Le message à envoyer
	 * @return Si le message à été envoyer true sinon false
	 */
	public Boolean envoyerMessage(int id_relation, String message) {
		URL url;
		Boolean resultat = false;
		try {
			message = URLEncoder.encode(message);
			url = new URL("https://trankillprojets.fr/wal/wal.php?ecrire&identifiant="+getId()+"&relation="+id_relation+"&message="+message);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestProperty("Content-Type", "application/json");
			String contentType = con.getHeaderField("Content-Type");

			BufferedReader in = new BufferedReader(
			  new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer content = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
			    content.append(inputLine);
			}
			in.close();
			
			JSONObject json = new JSONObject(content.toString());
			JSONObject etat = json.getJSONObject("etat");
			
			if(etat.getString("message").compareTo("message OK") != -1) {
				resultat = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resultat;
	}
	
	public HashMap<String,String> getMessage(int id_relation) {
		HashMap<String,String> discussions = new HashMap<String,String>();
		URL url;
		
		try {
			url = new URL("https://trankillprojets.fr/wal/wal.php?lire&identifiant="+getId()+"&relation="+id_relation);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestProperty("Content-Type", "application/json");
			String contentType = con.getHeaderField("Content-Type");
	
			BufferedReader in = new BufferedReader(
			  new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer content = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
			    content.append(inputLine);
			}
			in.close();
			
			JSONObject json = new JSONObject(content.toString());
			JSONArray messages = json.getJSONArray("messages");
			
			for(int i = 0; i < messages.length(); i++) {
				discussions.put(messages.getJSONObject(i).getString("identite"), messages.getJSONObject(i).getString("message"));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return discussions;
	}
	
	/**
	 * Créer une relation avec l'email en paramêtre
	 * @param email
	 * @return Si la relation est créé retourne true sinon false
	 */
	public Boolean ajouterContact(String email) {
		URL url;
		
		try {
			url = new URL("https://trankillprojets.fr/wal/wal.php?lier&identifiant="+getId()+"&mail="+email);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestProperty("Content-Type", "application/json");
			String contentType = con.getHeaderField("Content-Type");

			BufferedReader in = new BufferedReader(
			  new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer content = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
			    content.append(inputLine);
			}
			in.close();
			
			JSONObject json = new JSONObject(content.toString());
			JSONObject etat = json.getJSONObject("etat");
			
			if(etat.getString("message") == "association OK") {
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
