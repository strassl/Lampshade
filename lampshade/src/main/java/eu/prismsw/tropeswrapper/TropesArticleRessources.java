package eu.prismsw.tropeswrapper;

public class TropesArticleRessources {
	public String mainJS;
    public String noteJS;

	public TropesArticleRessources(String mainJS) {
		this.mainJS = mainJS;
        // Turns out tvtropes.org is a downright terrible mess. Found this between inlined google analytics code
        this.noteJS = "function togglenote(id){var ele=object(id); var state = ele.style.display; if(state==\"none\") ele.style.display=\"inline\"; if(state==\"inline\")ele.style.display=\"none\";}";
	}

}
