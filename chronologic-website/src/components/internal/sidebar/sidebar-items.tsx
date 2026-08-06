import { cn } from "@/lib/utils";

type SidebarItemProps = {
  id: string,
  label: string,
  children: React.ReactNode,
  activedItem: string
}

const SidebarItem = ({id, label, children, activedItem}: SidebarItemProps): React.ReactElement => {
  const active = activedItem === id;
  
  return (
    <div
      id={id}
      className={cn(
        "cursor-pointer pl-5 py-4 flex flex-row gap-4 items-center justify-start",
        "border-l-3 border-transparent",
        active ? "border-primary" : "hover:border-accent",
        active ? "bg-primary/15 text-primary" : "text-muted-foreground hover:bg-accent/50"
      )}
    >
      {children}
      <span className="font-heading">{label}</span>
    </div>
  );
}

export { SidebarItem };