package com.pkadev.pkaadventure.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.pkadev.pkaadventure.objects.PKABook;
import com.pkadev.pkaadventure.objects.PKAPage;
import com.pkadev.pkaadventure.objects.PKAPlayer;
import com.pkadev.pkaadventure.processors.PlayerProcessor;

public class BookUtil {

	public static PKABook getInitialPKABook(Player player, PKAPlayer pkaPlayer) {
		List<PKAPage> pages = new ArrayList<PKAPage>();
		pages.addAll(getInitialPages(pkaPlayer));
		
		return new PKABook(player, pages);
	}
	
	private static List<PKAPage> getInitialPages(PKAPlayer pkaPlayer) {
		List<PKAPage> pages = new ArrayList<PKAPage>();
		
		pages.add(getInitialAttributePage(pkaPlayer));
		pages.addAll(getInitialLocationListPages(pkaPlayer));
		pages.addAll(getInitialQuestListPages(pkaPlayer));
		pages.addAll(getInitialLocationInfoPages(pkaPlayer));
		pages.addAll(getInitialQuestInfoPages(pkaPlayer));
		
		return pages;
	}
	
	//accessed with getInitialInfoPages(PKAPlayer pkaPlayer)
	private static PKAPage getInitialAttributePage(PKAPlayer pkaPlayer) {
		String attributePage = ElementsUtil.getPageElement("AttributePage");
		String[] stringAttributes = new String[4];
		for (int i = 0; i < 4; i++) {
			stringAttributes[i] = "" + pkaPlayer.getAttributes()[i];
		}
		attributePage = String.format(attributePage, stringAttributes);
		PKAPage pkaPage = new PKAPage(attributePage);
		return pkaPage;
	}
		
	//accessed with getInitialInfoPages(PKAPlayer pkaPlayer)
	private static List<PKAPage> getInitialLocationListPages(PKAPlayer pkaPlayer) {
		List<PKAPage> pages = new ArrayList<PKAPage>();
		String firstPage = ElementsUtil.getPageElement("LocationPage");
		List<String> otherPages = new ArrayList<String>();
		if (firstPage.length() > 15)
		for (int i = 0; i < pkaPlayer.getDiscoveredLocations().size(); i++) {
			String discoveredLocation = pkaPlayer.getDiscoveredLocations().get(i);
			if (discoveredLocation.equals("default"))
				continue;
			if (i < 13) {
				firstPage += "- " + discoveredLocation + "\n";
			} else {
				int pageNumber = i / 14;
				String page = "";
				if (otherPages.contains(pageNumber)) {
					page = otherPages.get(pageNumber);
					page += "- " + discoveredLocation + "\n";
					otherPages.set(pageNumber, page);
				} else {
					otherPages.add("- " + discoveredLocation + "\n");
				}
				
				
			}
		}
		pages.add(new PKAPage(firstPage));
		for (int i = 0; i < otherPages.size(); i++) {
			pages.add(new PKAPage(otherPages.get(i)));
		}
		return pages;
	}
	
	//accessed with getInitialInfoPages(PKAPlayer pkaPlayer)
	private static List<PKAPage> getInitialLocationInfoPages(PKAPlayer pkaPlayer) {
		List<PKAPage> pages = new ArrayList<PKAPage>();
		for (int i = 0; i < pkaPlayer.getDiscoveredLocations().size(); i++) {
			String discoveredLocation = pkaPlayer.getDiscoveredLocations().get(i);
			String info = ElementsUtil.getLocationDescriptionElement(discoveredLocation.replace(' ', '_'));
			if (info == "")
				continue;
					
			String page = ElementsUtil.getPageElement("LocationInfoPage");
			page = String.format(page, discoveredLocation);
			pages.add(new PKAPage(page + info));
		}
		return pages;
	}
	
	//accessed with getInitialInfoPages(PKAPlayer pkaPlayer)
	private static List<PKAPage> getInitialQuestListPages(PKAPlayer pkaPlayer) {
		List<PKAPage> pages = new ArrayList<PKAPage>();
				
		return pages;
	}
	
	//accessed with getInitialInfoPages(PKAPlayer pkaPlayer)
	private static List<PKAPage> getInitialQuestInfoPages(PKAPlayer pkaPlayer) {
		List<PKAPage> pages = new ArrayList<PKAPage>();
		
		return pages;
	}
	
}
